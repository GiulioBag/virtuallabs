package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.exceptions.ImageException;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotHasTeamInCourseException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmModelExceptions.VMModelExcessiveLimitsException;
import it.polito.ai.virtuallabs.services.AssignmentService;
import it.polito.ai.virtuallabs.services.CourseService;
import it.polito.ai.virtuallabs.services.TeamService;
import it.polito.ai.virtuallabs.services.VMService;
import lombok.extern.java.Log;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("API/courses")
@Log (topic = "CourseController")
public class CourseController {

    @Autowired
    CourseService courseService;
    @Autowired
    AssignmentService assignmentService;

    @GetMapping({"", "/"})
    public List<CourseDTO> getCourses (){
        try {
            List<CourseDTO> courses = courseService.getCourses();
            for (CourseDTO cours : courses) {
                ModelHelper.enrich(cours);
            }
            return courses;
        } catch (RuntimeException e) {
            log.warning("getCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping({"","/"})
    public CourseDTO addCourse(Principal principal, @RequestBody CourseDTO dto){
        try{
            if(courseService.addCourse(dto, principal.getName())) {
                return ModelHelper.enrich(dto);
            }
        }catch(TeacherNotFoundException e){
            log.warning("addCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        log.warning("addCourse: " + dto.getName() + " already present");
        throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getName());
    }

    @PutMapping("/{courseName}")
    public CourseDTO updateCourse(Principal principal, @RequestBody CourseDTO dto, @PathVariable(name = "courseName") String courseName){
        try{
            if(courseService.updateCourse(courseName, dto, principal.getName()))
                return ModelHelper.enrich(dto);
        } catch(TeacherNotFoundException | CourseNotFoundException e){
            log.warning("updateCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e) {
            log.warning("updateCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
        log.warning("updateCourse: " + "Impossible change '" + courseName + "' with '" + dto.getName() + "' because it is already presents");
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible change '" + courseName + "' with '" + dto.getName() + "' because it is already presents");
    }

    @DeleteMapping("/{courseName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCourse(Principal principal, @PathVariable(name = "courseName") String courseName){
        try{
            courseService.deleteCourse(courseName, principal.getName());
        }catch (TeacherNotFoundException | CourseNotFoundException e){
            log.warning("deleteCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }catch (PermissionDeniedException e) {
            log.warning("deleteCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/addTeacher")
    @ResponseStatus(HttpStatus.OK)
    public void addTeacherToCourse(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody Map<String, String> input){
        try{
            if(input.containsKey("newTeacherId")){
                if(!courseService.addTeacherToCourse(courseName, principal.getName(), input.get("newTeacherId"))) {
                    log.warning("addTeacherToCourse: " + "Teacher '" + input.get("newTeacherId") + "' already owns the course.");
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher '" + input.get("newTeacherId") + "' already owns the course.");
                }
            }else {
                log.warning("addTeacherToCourse: wrong message received");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong message received");
            }
        }catch(TeacherNotFoundException | CourseNotFoundException e){
            log.warning("addTeacherToCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e){
            log.warning("addTeacherToCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/students")
    public List<StudentDTO> getStudentsByCourse(@PathVariable(name = "courseName") String courseName, Principal principal){
        try{
            List <StudentDTO> courseDTOS = courseService.getStudentsByCourse(courseName, principal);
            for (StudentDTO studentDTO : courseDTOS) {
                ModelHelper.enrich(studentDTO);
            }
            return courseDTOS;
        }catch (CourseNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("getStudentsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (PermissionDeniedException | StudentNotEnrolledToCourseException e){
            log.warning("getStudentsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{courseName}/enrollOne")
    public void enrollOne(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody Map<String, String> input){
        try{
            if(input.containsKey("studentId")){
                if(!courseService.enrollOneStudent(courseName, input.get("studentId"), principal.getName()))
                    log.warning("enrollOne: student already enrolled");
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already enrolled");
            }else{
                log.warning("enrollOne: wrong message received");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong message received");
            }
        } catch (StudentNotFoundException | TeacherNotFoundException | CourseNotFoundException e){
            log.warning("enrollOne: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e){
            log.warning("enrollOne: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/enrollMany")
    public List<Boolean> enrollMany(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody Map<String, List<String>> input){
        try {
            if (input.containsKey("studentIds"))
                return courseService.enrollManyStudents(courseName, input.get("studentsIds"), principal.getName());
            else {
                log.warning("enrollMany: wrong message received");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong message received");
        } catch (StudentNotFoundException | TeacherNotFoundException | CourseNotFoundException e) {
            log.warning("enrollMany: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e) {
            log.warning("enrollMany: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/disenrollOne")
    public void disenrollOne(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody Map<String, String> input){
        try{
            if(input.containsKey("studentId")){
                if(!courseService.disenrollOneStudent(courseName, input.get("studentId"), principal.getName()))
                    log.warning("disenrollOne: student already enrolled");
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already enrolled");
            }else{
                log.warning("disenrollOne: wrong message received");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong message received");
            }
        } catch (StudentNotFoundException | TeacherNotFoundException | CourseNotFoundException e){
            log.warning("disenrollOne: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e){
            log.warning("disenrollOne: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/disenrollMany")
    public List<Boolean> disenrollMany(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody Map<String, List<String>> input){
            try {
                if (input.containsKey("studentIds"))
                    return courseService.disenrollManyStudents(courseName, input.get("studentsIds"), principal.getName());
                else {
                    log.warning("disenrollMany: wrong message received");
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong message received");
            } catch (StudentNotFoundException | TeacherNotFoundException | CourseNotFoundException e) {
                log.warning("disenrollMany: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            } catch (PermissionDeniedException e) {
                log.warning("disenrollMany: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
    }

    @GetMapping("/{courseName}/vmmodel")
    public VMModelDTO getVMModel(Principal principal, @PathVariable(name = "courseName") String courseName){
        try{
            return courseService.getVMModel(courseName, principal.getName());
        }catch(CourseNotFoundException | TeacherNotFoundException e){
            log.warning("getVMModel: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{courseName}/vmmodel")
    @ResponseStatus(HttpStatus.OK)
    public void setVMModel(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody VMModelDTO vmModelDTO) {
        try {
            courseService.setVMModel(vmModelDTO, courseName, principal.getName());
        } catch (CourseNotFoundException | TeacherNotFoundException e) {
            log.warning("setVMModel: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e) {
            log.warning("setVMModel: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (VMModelExcessiveLimitsException e) {
            log.warning("setVMModel: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/enable")
    @ResponseStatus(HttpStatus.OK)
    public void enableCourse(Principal principal, @PathVariable(name = "courseName") String courseName){
        try {
            courseService.enableCourse(courseName, principal.getName());
        } catch (CourseNotFoundException | TeacherNotFoundException e){
            log.warning("enableCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e){
            log.warning("enableCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/disable")
    @ResponseStatus(HttpStatus.OK)
    public void disableCourse(Principal principal, @PathVariable(name = "courseName") String courseName){
        try {
            courseService.disableCourse(courseName, principal.getName());
        } catch (CourseNotFoundException | TeacherNotFoundException e){
            log.warning("disableCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PermissionDeniedException e){
            log.warning("disableCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/team")
    public Optional<TeamDTO> getTeamByCourse(@PathVariable (name = "courseName") String courseName, Principal principal){
        try {
            Optional<TeamDTO> optTeamDTO = courseService.getTeamByCourse(courseName, principal);
            optTeamDTO.ifPresent(ModelHelper::enrich);
            return optTeamDTO;

        } catch (CourseNotFoundException | StudentNotFoundException e) {
            log.warning("getTeamByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotEnrolledToCourseException e) {
            log.warning("getTeamByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/teams")
    public List<TeamDTO> getTeamsByCourse(Principal principal, @PathVariable(name = "courseName") String courseName){
        try{
            return courseService.getTeamsByCourse(courseName, principal.getName()).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(TeacherNotFoundException | CourseNotFoundException e){
            log.warning("getTeamsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch(PermissionDeniedException e){
            log.warning("getTeamsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/freeStudents")
    public List<StudentDTO> possibleTeamMembers(@PathVariable (name = "courseName") String courseName, Principal principal){
        try {
            List<StudentDTO> studentDTOList = courseService.possibleTeamMember(courseName, principal);
            for (StudentDTO studentDTO : studentDTOList) {
                ModelHelper.enrich(studentDTO);
            }
            return studentDTOList;

        } catch (CourseNotFoundException  e ){
            log.warning("possibleTeamMembers: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (StudentNotEnrolledToCourseException e ){
            log.warning("possibleTeamMembers: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/proposedTeams")
    public List<TeamDTO> proposedTeamByCourse(@PathVariable (name = "courseName") String courseName, Principal principal){
        try {
            List<TeamDTO> teamDTOList = courseService.getProposedTeamByCourse(courseName, principal);
            for (TeamDTO teamDTO : teamDTOList) {
                ModelHelper.enrich(teamDTO);
            }
            return teamDTOList;

        } catch (CourseNotFoundException | StudentNotFoundException e) {
            log.warning("proposedTeamByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (StudentNotEnrolledToCourseException e) {
            log.warning("proposedTeamByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/vms")
    public List<VMDTO> getVMS(@PathVariable(name = "courseName") String courseName, Principal principal) {
        try {
            List<VMDTO>  vmdtoList = courseService.getVMSByCourse(courseName, principal);
            for (VMDTO vmdto : vmdtoList) {
                ModelHelper.enrich(vmdto);
            }
            return vmdtoList;
        } catch (CourseNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("getVMS: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch ( StudentNotHasTeamInCourseException e){
            log.warning("getVMS: " + e.getMessage());
            throw  new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (StudentNotEnrolledToCourseException  e){
            log.warning("getVMS: " + e.getMessage());
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/insertAssignment")
    @ResponseStatus(HttpStatus.OK)
    public void insertAssignment(Principal principal, @PathVariable(name = "courseName") String courseName, @RequestBody AssignmentDTO assignmentDTO){
        try{
            assignmentService.insertAssignment(assignmentDTO, courseName, principal.getName());
        }catch(TeacherNotFoundException | CourseNotFoundException | IOException | ImageException e){
            log.warning("insertAssignment: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/assignments")
    public List<AssignmentDTO> getAssignmentsByCourse(Principal principal, @PathVariable(name = "courseName") String courseName){
        try{
            return courseService.getAssignmentsByCourse(principal, courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(CourseNotFoundException | TeacherNotFoundException | StudentNotFoundException e){
            log.warning("getAssignmentsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (PermissionDeniedException | StudentNotEnrolledToCourseException e){
            log.warning("getAssignmentsByCourse: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}
