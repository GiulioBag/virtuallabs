package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.CourseDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;

import java.security.Principal;
import java.util.List;

public interface StudentService {

    // eventualmnte mettere qualcosa del tipo getAllCurse o getAllTeam
    List<StudentDTO> getStudents();

    List<CourseDTO> getCourses(Principal principal);
}
