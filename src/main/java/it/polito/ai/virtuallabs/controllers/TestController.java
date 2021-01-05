package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.repositories.StudentRepository;
import it.polito.ai.virtuallabs.repositories.TeacherRepository;
import it.polito.ai.virtuallabs.repositories.TeamRepository;
import it.polito.ai.virtuallabs.services.UtilitsService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@Log(topic = "TestController")
public class TestController {

    @Autowired
    UtilitsService utilitsService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping(value = {"", "/"})
    public void test() throws IOException {

        log.info("asdfgjhfdsa");
    }
}
