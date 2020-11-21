package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
import org.springframework.hateoas.Link;

public class ModelHelper {
    public static CourseDTO enrich(CourseDTO courseDTO){
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/students").withRel("enrolled"));
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/vmmodel").withRel("vmModel"));
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/assignments").withRel("assignments"));
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/teachers").withRel("teachers"));
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/freestudents").withRel("freestudents"));
        courseDTO.add(new Link("http://localhost:8080/API/courses/" + courseDTO.getName() + "/vms").withRel("vms"));


        return courseDTO;
    }

    public static StudentDTO enrich(StudentDTO studentDTO){
        return studentDTO;
    }

    public static TeamDTO enrich(TeamDTO teamDTO){
        teamDTO.add(new Link("http://localhost:8080/API/teams/" + teamDTO.getName() + "/members").withRel("members"));
        teamDTO.add(new Link("http://localhost:8080/API/teams/" + teamDTO.getName() + "/vms").withRel("vms"));
        return teamDTO;
    }

    public static VMDTO enrich(VMDTO vmdto){
        return vmdto;
    }

    public static AssignmentDTO enrich(AssignmentDTO assignmentDTO){
        assignmentDTO.add(new Link("http://localhost:8080/API/assignments/" + assignmentDTO.getName() + "/papers").withRel("papers"));
        return assignmentDTO;
    }

    public static PaperDTO enrich(PaperDTO paperDTO){
        paperDTO.add(new Link("http://localhost:8080/API/papers/" + paperDTO.getId() + "/student").withRel("student"));
        paperDTO.add(new Link("http://localhost:8080/API/papers/" + paperDTO.getId() + "/history").withRel("history"));
        paperDTO.add(new Link("http://localhost:8080/API/papers/" + paperDTO.getId() + "/lastVersion").withRel("lastVersion"));

        return paperDTO;
    }

    public static DeliveredPaperDTO enrich(DeliveredPaperDTO dpDTO) {
        return dpDTO;
    }
}
