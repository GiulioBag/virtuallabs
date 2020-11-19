package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotHasTeamInCourseException extends StudentException {
    public StudentNotHasTeamInCourseException (String studentId, String courseID){
        super(studentId + " student does not belong to any a team of " + courseID + " course");
    }
}
