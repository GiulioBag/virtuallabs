package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.exceptions.ImageException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.ExpiredAssignmentException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotCheckableException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentHasNotPaper;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.services.AssignmentService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
        }catch (TeacherNotFoundException | PaperNotFoundException | IOException e) {
            log.warning("getStudentByPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (PermissionDeniedException e){
            log.warning("getStudentByPaper: " + e.getMessage() );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{paperId}/history")
    public List<DeliveredPaperDTO> getHistoryByPaper(Principal principal, @PathVariable(name = "paperId") String paperId){
        try {
            return assignmentService.getHistoryByPaper(paperId, principal.getName()).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (TeacherNotFoundException | PaperNotFoundException | StudentNotFoundException e) {
            log.warning("getStudentByPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException | StudentHasNotPaper e) {
            log.warning("getStudentByPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{paperId}/delivery")
    @ResponseStatus(HttpStatus.OK)
    public void insertDeliveredPaper(Principal principal, @PathVariable(name = "paperId") String paperId, @RequestBody ContentDTO contentDTO){
        try {
            assignmentService.insertPaper(contentDTO, paperId, principal);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot open the image.");
        } catch (ExpiredAssignmentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{paperId}/lastVersion")
    public DeliveredPaperDTO getLastVersion(Principal principal, @PathVariable(name = "paperId") String paperId){
        try{
            return ModelHelper.enrich(assignmentService.getLastVersion(principal.getName(), paperId));
        }catch (TeacherNotFoundException | PaperNotFoundException | IOException | ImageException e){
            log.warning("getLastVersion: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch(PermissionDeniedException e){
            log.warning("getLastVersion: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{paperId}/check")
    @ResponseStatus(HttpStatus.OK)
    public void checkPaper(@RequestBody EvaluatedPaperDTO evaluatedPaperDTO, @PathVariable(name = "paperId") String paperId, Principal principal) {
        try {

            assignmentService.checkPaper(evaluatedPaperDTO, principal.getName(), paperId);

        } catch (TeacherNotFoundException | PaperNotFoundException | IOException | ImageException e) {
            log.warning("checkPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException | PaperNotCheckableException e) {
            log.warning("checkPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{paperId}/assignment")
    public AssignmentDTO getAssignment(@PathVariable(name = "paperId") String paperId, Principal principal) {
        try {
            return ModelHelper.enrich(assignmentService.getAssignmentbyPaper(paperId, principal));
        } catch (PermissionDeniedException e) {
            log.warning("checkPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (TeacherNotFoundException | PaperNotFoundException | StudentNotFoundException e) {
            log.warning("checkPaper: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
