package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
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

    public static AssignmentDTO enrich(AssignmentDTO assignmentDTO){
        return assignmentDTO;
    }

    public static PaperDTO enrich(PaperDTO paperDTO){
        //studentDTO.add(new Link("http://localhost:8080/API/students/" + studentDTO.getId()));
        return paperDTO;
    }

    public static DeliveredPaperDTO enrich(DeliveredPaperDTO dpDTO) {
        //studentDTO.add(new Link("http://localhost:8080/API/students/" + studentDTO.getId()));
        return dpDTO;
    }
}
