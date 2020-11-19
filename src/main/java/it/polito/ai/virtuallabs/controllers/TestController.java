package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.DeliveredPaperDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.entities.Teacher;
import it.polito.ai.virtuallabs.entities.Team;
import it.polito.ai.virtuallabs.repositories.StudentRepository;
import it.polito.ai.virtuallabs.repositories.TeacherRepository;
import it.polito.ai.virtuallabs.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("")
    StudentDTO test (){

        Student student = studentRepository.getByUser_SerialNumber("s2");
        System.out.println("Chiave: " + student.getId());
        studentRepository.save(student);
        System.out.println("Chiave: " + student.getId());
        studentRepository.save(student);
        System.out.println("Chiave: " + student.getId());
        return modelMapper.map(studentRepository.getByUser_SerialNumber("s2"), StudentDTO.class);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public void test(@RequestBody DeliveredPaperDTO deliveredPaperDTO){
        System.out.println("tutto bene");
    }
}
