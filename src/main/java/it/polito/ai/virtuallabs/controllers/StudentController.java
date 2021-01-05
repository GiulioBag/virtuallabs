package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.services.StudentService;
import it.polito.ai.virtuallabs.services.VMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private VMService vmService;

    @GetMapping({"", "/"})
    public List<StudentDTO> getCourses() {
        return studentService.getStudents().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

}












