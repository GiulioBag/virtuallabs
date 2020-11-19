package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.CourseDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.services.TeacherService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("API/teachers")
@Log (topic = "TeacherController")
public class TeacherController {

    @Autowired
    TeacherService teacherService;


    @GetMapping("/{teacherId}/courses")
    public List<CourseDTO> getCoursesByTeacher (@PathVariable(name = "teacherId") String teacherId){
        try{
            List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
            for (CourseDTO cours : courses) {
                ModelHelper.enrich(cours);
            }
            return courses;
        }catch(TeacherNotFoundException e){
        log.warning("getCoursesByTeacher: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
