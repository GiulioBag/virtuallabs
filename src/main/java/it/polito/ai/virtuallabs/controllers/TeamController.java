package it.polito.ai.virtuallabs.controllers;


import it.polito.ai.virtuallabs.dtos.ProposedTeamDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.exceptions.MyException;
import it.polito.ai.virtuallabs.exceptions.studentException.*;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamAlreadyExistException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamExpiredException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotActivedException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotFoundException;
import it.polito.ai.virtuallabs.services.TeamService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/API/teams")
@Log (topic = "TeamController")

public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getTeamStudentByTeam(@PathVariable(name = "teamId") String teamId, Principal principal){
        try {
            List<StudentDTO> studentDTOList =  teamService.getTeamStudentByTeam(teamId, principal);
            for (StudentDTO studentDTO : studentDTOList) {
                ModelHelper.enrich(studentDTO);
            }
            return studentDTOList;

        } catch (StudentNotFoundException | TeamNotFoundException e) {
            log.warning("getTeamStudentByTeam " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (TeamExpiredException  e) {
            log.warning("getTeamStudentByTeam " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (StudentNotEnrolledToCourseException | StudentNotBelongToTeam e) {
            log.warning("getTeamStudentByTeam " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    //TODO fare attenzione a come deve essere passata la data
    @PostMapping({"/", ""})
    @ResponseStatus(HttpStatus.OK)
    public void newTeam(@RequestBody ProposedTeamDTO proposedTeamDTO, @PathVariable(name = "courseName") String courseName, Principal principal) {
        try {
            teamService.proposeTeam(proposedTeamDTO, courseName, principal);

        } catch (TeamAlreadyExistException | StudentAlreadyInTeamException e) {
            log.warning("newTeam: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (StudentNotEnrolledToCourseException | StudentNotHasTeamInCourseException e) {
            log.warning("newTeam: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (MyException e) {
            log.warning("newTeam: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/confirmTeam")
    @ResponseStatus(HttpStatus.OK)
    public void confirmTeamParticipation(@PathVariable(name = "teamId") String teamID, Principal principal){
        try {
            teamService.confirmTeamParticipation(teamID, principal);
        } catch (TeamNotFoundException | StudentNotFoundException | TeamExpiredException e) {
            log.warning("confirmTeamParticipation: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotInvitedToTeamException  | StudentAlreadyInTeamException e) {
            log.warning("confirmTeamParticipation: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (StudentAlreadyAcceptedTeamException e) {
            log.warning("confirmTeamParticipation: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/rejectedTeam")
    @ResponseStatus(HttpStatus.OK)
    public void rejectTeamParticipation(@PathVariable(name = "teamId") String teamID, Principal principal){
        try {
            teamService.rejectTeamParticipation(teamID, principal);
        } catch (TeamNotFoundException | StudentNotFoundException e) {
            log.warning("rejectTeamParticipation: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (StudentNotInvitedToTeamException e) {
            log.warning("rejectTeamParticipation: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    // Only teacher
    @GetMapping("/{teamName}/vms")
    public List<VMDTO> getVMSByTeam(@PathVariable(name = "teamName") String teamName, Principal principal) {
        try {
            return teamService.getVMsByTeam(teamName, principal);
        } catch (TeamNotFoundException | TeamExpiredException e){
            log.warning("getVMSByTeam: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (PermissionDeniedException | StudentNotBelongToTeam | TeamNotActivedException e){
            log.warning("getVMSByTeam: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
