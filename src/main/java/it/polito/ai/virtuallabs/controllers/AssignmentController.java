package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs.dtos.PaperDTO;
import it.polito.ai.virtuallabs.dtos.TeacherDTO;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.services.AssignmentService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.RefreshFailedException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("API/assignments")
@Log(topic = "AssignmentController")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @GetMapping("/{assignmentId}/papers")
    public List<PaperDTO> getPapersByAssignment(Principal principal, @PathVariable(name = "assignmentId") String assignmentId){
        try{
            return assignmentService.getPapersByAssignment(principal, assignmentId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(AssignmentNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("getPapersByAssignment: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch(PermissionDeniedException e){
            log.warning("getPapersByAssignment: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{assignmentId}/teacher")
    public TeacherDTO getTeacherByAssignment(Principal principal, @PathVariable(name = "assignmentId") String assignmentId){
        try{
            return assignmentService.getTeacherByAssignment(principal, assignmentId);
        }catch(AssignmentNotFoundException | TeacherNotFoundException | StudentNotFoundException e) {
            log.warning("getPapersByAssignment: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
