package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.CourseDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.TeamDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;
import org.springframework.hateoas.Link;

public class ModelHelper {
    public static CourseDTO enrich(CourseDTO courseDTO){
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName()));
        //courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/enrolled").withRel("enrolled"));
        return courseDTO;
    }

    public static StudentDTO enrich(StudentDTO studentDTO){
        //studentDTO.add(new Link("http://localhost:8080/API/students/" + studentDTO.getId()));
        return studentDTO;
    }

    public static TeamDTO enrich(TeamDTO teamDTO){
        //studentDTO.add(new Link("http://localhost:8080/API/students/" + studentDTO.getId()));
        return teamDTO;
    }

    public static VMDTO enrich(VMDTO vmdto){
        //studentDTO.add(new Link("http://localhost:8080/API/students/" + studentDTO.getId()));
        return vmdto;
    }
}
