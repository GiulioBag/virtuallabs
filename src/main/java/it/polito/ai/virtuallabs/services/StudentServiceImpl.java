package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.CourseDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    VMRepository vmRepository;

    @PreAuthorize("hasRole('TEACHER')")
    public List<StudentDTO> getStudents() {

        return userRepository.findAllByRolesEquals("ROLE_STUDENT")
                .stream()
                .map(i -> modelMapper.map(i, StudentDTO.class))
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('STUDENT')")
    public List<CourseDTO> getCourses(Principal principal) {

        Student student = studentRepository.getByUser_SerialNumber(principal.getName());
        return student.getCourses().stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList());
    }
}
