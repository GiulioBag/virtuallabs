package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.AuthenticationRequestDTO;
import it.polito.ai.virtuallabs.dtos.UserDTO;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.exceptions.confirmTokenException.ConfirmTokenExpiredException;
import it.polito.ai.virtuallabs.exceptions.confirmTokenException.ConfirmTokenNotFoundException;
import it.polito.ai.virtuallabs.exceptions.userException.*;
import it.polito.ai.virtuallabs.repositories.*;
import it.polito.ai.virtuallabs.security.jwt.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ConfirmTokenRepository confirmTokenRepository;

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    UtilitsService utilitsService;

    private static final String STUDENT_ROLE = "ROLE_STUDENT";
    private static final String TEACHER_ROLE = "ROLE_TEACHER";

    @Override
    public UserDTO getUser(String userId) throws IOException {

        // check if users exists
        Optional<User> optUser = userRepository.findById(userId);

        if(optUser.isEmpty()){
            throw new UserNotFoundException(userId);
        }

        return fromEntityToDTO(optUser.get());
    }

    @Override
    public void addUser(UserDTO userDTO) throws IOException {

        // Check if last name and name are not null
        if (userDTO.getLastName() == null) {
            throw new BadFieldValueException("last name");
        }

        if (userDTO.getName() == null) {
            throw new BadFieldValueException("name");
        }

        /* We check first the student regular expressions because in a real system there are
         more students than teachers */

        // Check password structure
        String password = userDTO.getPassword();
        if (!(password.matches(".*\\d.*") && password.length() > 6 && password.matches(".*[A-Z].*"))) {
            throw new BadFieldValueException("password");
        }

        // Check email structure, the digits after the last name are used to manage homonyms
        String email = userDTO.getEmail();
        if (!(email.matches("s[0-9]+@studenti.polito.it") || email.matches("[A-Z]?[a-z]+\\.[A-Z]?[a-z]+[0-9]*@polito.it"))) {
            throw new BadFieldValueException("email");
        }

        // Check serial number structure
        String serialNumber = userDTO.getSerialNumber();
        if (!(serialNumber.matches("s[0-9]+") || serialNumber.matches("d[0-9]+"))) {
            throw new BadFieldValueException("serial number");
        }

        // Check if the user is already registered
        if (userRepository.existsById(serialNumber)) {
            throw new SerialNumberAlreadyExistException();
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException();
        }

        User user = fromDTOtoEntity(userDTO);
        user.setPassword(encoder.encode(password));
        user.setActive(false);

        // Student case
        if (serialNumber.startsWith("s") && email.equals(serialNumber + "@studenti.polito.it")) {
            Student student = new Student(user);
            notificationService.sendConfirmMessage(email, "student", serialNumber);
            user.getRoles().add(STUDENT_ROLE);
            studentRepository.save(student);
            return;
        }

        // Teacher case
        if (serialNumber.startsWith("d") && email.startsWith(user.getName().toLowerCase() + "."
                + user.getLastName().toLowerCase()) && email.endsWith("@polito.it")) {
            Teacher teacher = new Teacher(user);
            notificationService.sendConfirmMessage(user.getEmail(), "teacher", serialNumber);
            user.getRoles().add(TEACHER_ROLE);
            teacherRepository.save(teacher);
            return;
        }

        // Wrong serial number/email combination.
        throw new BadSerialNuimberEmailCombinationException();

    }

    @Override
    public void confirmRegistration(String tokeID) {

        Optional<ConfirmToken> optConfirmToken = confirmTokenRepository.findById(tokeID);

        // Check if token is present
        if (optConfirmToken.isPresent()) {
            ConfirmToken confirmToken = optConfirmToken.get();
            confirmTokenRepository.deleteById(tokeID);

            // Check if token is expired
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            if (confirmToken.getExpiryDate().compareTo(timestamp) > 0) {

                // Token is not expired
                String serialNumber = confirmToken.getSerialNumber();
                Optional<User> optUser = userRepository.findById(serialNumber);

                if (optUser.isPresent()) {
                    User user = optUser.get();
                    user.setActive(true);
                    userRepository.save(user);

                } else {
                    throw new UsernameNotFoundException(serialNumber);
                }
            } else {
                throw new ConfirmTokenExpiredException();
            }
        } else {
            throw new ConfirmTokenNotFoundException();
        }
    }

    @Override
    public ResponseEntity<Map<Object, Object>> signin (AuthenticationRequestDTO data){

        String username = data.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
        String token = jwtTokenProvider.createToken(username, userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("token", token);
        return ok(model);

    }

    @Override
    public void deleteToken(String tokenID)  {
        JwtBlacklist jwt = new JwtBlacklist(tokenID.substring(7), new Date());
        jwtBlacklistRepository.save(jwt);
    }

    private User fromDTOtoEntity(UserDTO userDTO) throws IOException {
        User user = new User();
        user.setSerialNumber(userDTO.getSerialNumber());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setRoles(new ArrayList<>());

        String path = "users/" + user.getSerialNumber();
        utilitsService.fromImageToPath(userDTO.getPhoto(), path);
        return user;
    }

    private UserDTO fromEntityToDTO(User user) throws IOException {
        UserDTO userDto = new UserDTO();
        userDto.setSerialNumber(user.getSerialNumber());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setLastName(user.getLastName());
        String path = "users/" + user.getSerialNumber();
        userDto.setPhoto( utilitsService.fromPathToImage(path));
        return userDto;
    }

}
