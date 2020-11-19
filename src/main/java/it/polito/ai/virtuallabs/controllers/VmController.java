package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotBelongToTeam;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotHasTeamInCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotOwnVMException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamExpiredException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotActivedException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmException.*;
import it.polito.ai.virtuallabs.services.VMService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/API/vm")
@Log(topic = "VMsController")

public class VmController {

    @Autowired
    VMService vmService;

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public void vmChangeParam (@RequestBody VMDTO vmdto, Principal principal){
        try{
            vmService.vmChangeParam(vmdto, principal);
        } catch (VmNotFoundException | TeacherNotFoundException | StudentNotFoundException | VmParameterException e){
            log.warning("vmChangeParam: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotOwnVMException | ReachedMaximumTotalValueException e){
            log.warning("vmChangeParam: " + e.getClass());
            throw  new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{vmid}/{action}")
    @ResponseStatus(HttpStatus.OK)
    public void changeState(@PathVariable (name = "vmid") String vmId, @PathVariable (name = "action") String action,
                           Principal principal){
        try{
            vmService.changeState(vmId, action, principal);
        } catch (NotAllowedActionException | VmNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("changeState: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotOwnVMException | MaxActiveVmException | PermissionDeniedException | VmCourseNotActive e){
            log.warning("changeState: " + e.getClass());
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }


    @DeleteMapping("/{vmid}")
    @ResponseStatus(HttpStatus.OK)
    public void vmDelete(@PathVariable (name = "vmid") String vmId, Principal principal){
        try{
            vmService.deleteVM(vmId, principal);
        } catch ( VmNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("vmDelete: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotOwnVMException  | PermissionDeniedException | VmCourseNotActive e){
            log.warning("vmDelete: " + e.getClass());
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    // Only student
    @PostMapping("/create/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public void vmCreate(@PathVariable (name = "courseId") String courseId, @RequestBody VMDTO vmdto,
                             Principal principal){
        try{
             vmService.createVM(vmdto, courseId, principal);
        } catch ( StudentNotHasTeamInCourseException | StudentNotFoundException | VmParameterException |
                TeamExpiredException  e){
            log.warning("vmCreate: " + e.getClass());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MaxVmException | ReachedMaximumTotalValueException e){
            log.warning("vmCreate: " + e.getClass());
            throw  new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (TeamNotActivedException e){
            log.warning("vmCreate: " + e.getClass());
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

    }




}
