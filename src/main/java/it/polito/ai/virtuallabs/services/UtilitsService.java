package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.entities.*;

import java.io.IOException;
import java.security.Principal;

public interface UtilitsService {

    // STUDENT
    Student checkStudent(String studentId);

    // TEAM
    Team checkTeam (String teamId);
    void checkTeamExpired(Team team, boolean activeException);
    void checkTeamActive(Team team);
    void removeTeam(Team team);

    // COURSE
    void  checkCourseActive(String vmId);

    // VM
    VM checkExistingVMCondition(String vmId, Principal principal);
    int checkVMValue(int newValue, int oldValue, int totValue, int maxVal, String param, VM vm);
    void updateVM(Team team, VMDTO vmdto, VM vm);

    // TEACHER
    Teacher checkTeacher(String teachId);
    VM checkTeacherCondition(String  vmdtoID, Principal principal);

    // ASSIGNMENT
    Assignment checkAssignment(String assignmentId);
    void checkExpiredAssignmentByPaper(Paper paper, boolean activeException);
    void checkExpiredAssignment(Assignment assignment, boolean activeException);

    // PAPER
    Paper checkPaper (String paperId);

    // COURSE
    Course checkCourse(String courseName);
    Course checkCourseOwner (String courseName, String teacherId);

    void fromImageToPath(byte[] image, String path) throws IOException;
    byte[] fromPathToImage(String path) throws IOException;
}
