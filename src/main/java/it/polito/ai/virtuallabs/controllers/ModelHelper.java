package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
import org.springframework.hateoas.Link;


public class ModelHelper {

    //public static String rootPath = "http://192.168.10.10:8080/API";
    public static String rootPath = "http://localhost:4200/API";

    public static CourseDTO enrich(CourseDTO courseDTO) {
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName()).withRel("self"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/students").withRel("enrolled"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/vmmodel").withRel("vmmodel"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/assignments").withRel("assignments"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/teachers").withRel("teachers"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/freeStudents").withRel("freestudents"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/vms").withRel("vms"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/teams").withRel("teams"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/proposedTeams").withRel("proposedteams"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/team").withRel("team"));
        courseDTO.add(new Link(rootPath + "/courses/" + courseDTO.getName() + "/myvms").withRel("myvms"));
        return courseDTO;
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        return studentDTO;
    }

    public static UserDTO enrich(UserDTO studentDTO) {
        return studentDTO;
    }

    public static TeamDTO enrich(TeamDTO teamDTO) {
        teamDTO.add(new Link(rootPath + "/teams/" + teamDTO.getId() + "/members").withRel("members"));
        teamDTO.add(new Link(rootPath + "/teams/" + teamDTO.getId() + "/proposedMembers").withRel("proposedmembers"));
        teamDTO.add(new Link(rootPath + "/teams/" + teamDTO.getId() + "/vms").withRel("vms"));
        if (!teamDTO.isActive()) {
            teamDTO.add(new Link(rootPath + "/teams/" + teamDTO.getId() + "/confirm").withRel("confirm"));
            teamDTO.add(new Link(rootPath + "/teams/" + teamDTO.getId() + "/reject").withRel("reject"));
        }
        return teamDTO;
    }

    public static VMDTO enrich(VMDTO vmdto) {
        vmdto.add(new Link(rootPath + "/vms/").withRel("change"));
        vmdto.add(new Link(rootPath + "/vms/" + vmdto.getId() + "/on").withRel("on"));
        vmdto.add(new Link(rootPath + "/vms/" + vmdto.getId() + "/off").withRel("off"));
        vmdto.add(new Link(rootPath + "/vms/" + vmdto.getId()).withRel("delete"));
        vmdto.add(new Link(rootPath + "/vms/" + vmdto.getId() + "exec").withRel("exec"));
        return vmdto;
    }

    public static AssignmentDTO enrich(AssignmentDTO assignmentDTO) {
        assignmentDTO.add(new Link(rootPath + "/assignments/" + assignmentDTO.getName() + "/papers").withRel("papers"));
        assignmentDTO.add(new Link(rootPath + "/assignments/" + assignmentDTO.getName() + "/teacher").withRel("teacher"));
        return assignmentDTO;
    }

    public static PaperDTO enrich(PaperDTO paperDTO) {
        paperDTO.add(new Link(rootPath + "/papers/" + paperDTO.getId() + "/student").withRel("student"));
        paperDTO.add(new Link(rootPath + "/papers/" + paperDTO.getId() + "/history").withRel("history"));
        paperDTO.add(new Link(rootPath + "/papers/" + paperDTO.getId() + "/lastVersion").withRel("lastVersion"));

        return paperDTO;
    }

    public static DeliveredPaperDTO enrich(DeliveredPaperDTO dpDTO) {
        return dpDTO;
    }

    public static VMModelDTO enrich(VMModelDTO vmModelDTO, String courseName) {
        vmModelDTO.add(new Link(rootPath + "/" + courseName + "/vmmodel").withRel("set"));
        return vmModelDTO;
    }
}
