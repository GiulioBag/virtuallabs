package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.DeliveredPaperDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.ExpiredAssignmentException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.MissFiledDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.WrongStutusDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotChangeableException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.services.AssignmentService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("API/papers")
@Log(topic = "PaperController")
public class PaperController {

    @Autowired
    AssignmentService assignmentService;

    @GetMapping("/{paperId}/student")
    public StudentDTO getStudentByPaper(Principal principal, @PathVariable(name = "paperId") String paperId){
        try{
            return ModelHelper.enrich(assignmentService.getStudentByPaper(principal.getName(), paperId));
        }catch (TeacherNotFoundException | PaperNotFoundException e) {
            log.warning("getStudentByPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (PermissionDeniedException e){
            log.warning("getStudentByPaper: " + e.getMessage() );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{paperId}/history")
    public List<DeliveredPaperDTO> getHistoryByPaper(Principal principal, @PathVariable(name = "paperId") String paperId){
        try{
            return assignmentService.getHistoryByPaper(paperId, principal.getName()).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch (TeacherNotFoundException | PaperNotFoundException e) {
            log.warning("getStudentByPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (PermissionDeniedException e){
            log.warning("getStudentByPaper: " + e.getMessage() );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{paperId}/delivery")
    @ResponseStatus(HttpStatus.OK)
    public void insertDeliveredPaper(Principal principal, @PathVariable(name = "paperId") String paperId, @RequestBody DeliveredPaperDTO deliveredPaperDTO){
        try{
            assignmentService.insertPaper(deliveredPaperDTO, paperId, principal);
        }catch (StudentNotFoundException | PaperNotFoundException | MissFiledDeliveredPaperException | AssignmentNotFoundException e){
            log.warning("insertDeliveredPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }catch (ExpiredAssignmentException | PaperNotChangeableException | StudentNotEnrolledToCourseException | WrongStutusDeliveredPaperException e){
            log.warning("insertDeliveredPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
