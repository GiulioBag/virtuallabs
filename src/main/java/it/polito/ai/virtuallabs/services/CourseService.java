package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<TeamDTO> getProposedTeamByCourse(String courseName, Principal principal);
    List<StudentDTO> possibleTeamMember(String courseName, Principal principal) throws IOException;

    Optional<TeamDTO> getTeamByCourse(String courseName, Principal principal);
    List<TeamDTO> getTeamsByCourse(String courseName, String teacherId);

    List<CourseDTO> getCourses();

    boolean addCourse(CourseDTO courseDTO, String teacherId);

    boolean updateCourse(String courseName, CourseDTO courseDTO, String teacherId);

    void deleteCourse(String courseName, String teacherId);

    boolean addTeacherToCourse(String courseName, String teacherId, String newTeacherId);

    void enableCourse(String courseName, String teacherId);

    void disableCourse(String courseName, String teacherId);

    // - enrollment/disenrollment
    List<StudentDTO> getStudentsByCourse(String courseName, Principal principal);

    UserDTO enrollOneStudent(String courseName, String studentId, String teacherId);

    List<UserDTO> enrollManyStudents(String courseName, List<String> studentIds, String teacherId);

    // qui fare solo enroll from csv
    // ?? List<Boolean> addAndEnrollManyStudentsFromCSV(Reader r, String courseName, String teacherId);
    boolean disenrollOneStudent(String courseName, String studentId, String teacherId);

    List<Boolean> disenrollManyStudents(String courseName, List<String> studentIds, String teacherId);

    // - VM Model management
    void setVMModel(VMModelDTO vmModelDTO, String courseName, String teacherId);

    Optional<VMModelDTO> getVMModel(String courseName, String teacherId);

    // vm
    List<VMDTO> getVMSByCourse(String courseName, Principal principal);

    //assignment
    List<AssignmentDTO> getAssignmentsByCourse(Principal principal, String courseName);



}
