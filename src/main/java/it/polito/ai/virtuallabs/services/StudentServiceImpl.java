package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


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
}
