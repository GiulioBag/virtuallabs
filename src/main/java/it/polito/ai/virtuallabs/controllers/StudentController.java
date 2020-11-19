package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.exceptions.MyException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentAlreadyInTeamException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotInvitedToTeamException;
import it.polito.ai.virtuallabs.exceptions.teamException.*;
import it.polito.ai.virtuallabs.services.StudentService;
import it.polito.ai.virtuallabs.services.VMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private VMService vmService;

}












