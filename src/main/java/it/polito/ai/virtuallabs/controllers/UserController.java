package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.AuthenticationRequestDTO;
import it.polito.ai.virtuallabs.dtos.UserDTO;
import it.polito.ai.virtuallabs.exceptions.ImageException;
import it.polito.ai.virtuallabs.exceptions.userException.EmailAlreadyExistException;
import it.polito.ai.virtuallabs.exceptions.userException.SerialNumberAlreadyExistException;
import it.polito.ai.virtuallabs.exceptions.userException.UserException;
import it.polito.ai.virtuallabs.exceptions.userException.UserNotFoundException;
import it.polito.ai.virtuallabs.repositories.UserRepository;
import it.polito.ai.virtuallabs.security.jwt.JwtTokenProvider;
import it.polito.ai.virtuallabs.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/API/users")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable(name = "userId") String userId) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            userDTO.setPassword("");
            return userDTO;
        } catch (UserNotFoundException | IOException | ImageException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(value = "/signup")
    @ResponseStatus(HttpStatus.OK)
    public void addUser(@RequestBody UserDTO user) {
        try {
            userService.addUser(user);
        } catch (EmailAlreadyExistException | SerialNumberAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UserException | NullPointerException | IOException | ImageException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signin(@RequestBody AuthenticationRequestDTO data) {
        System.out.println("Signin started..");
        try {
            return userService.signin(data);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid serialnumber/password.");
        }
    }

    @GetMapping("/signout")
    @ResponseStatus(HttpStatus.OK)
    public void signout(@RequestHeader(name = "Authorization") String token) {
        userService.deleteToken(token);
    }

}
