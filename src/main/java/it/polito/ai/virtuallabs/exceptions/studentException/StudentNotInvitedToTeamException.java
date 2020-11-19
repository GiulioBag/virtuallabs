package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotInvitedToTeamException extends StudentException {
    public StudentNotInvitedToTeamException (String studentId, String teamId){
        super("The student: " + studentId + " is not invited to " + teamId + "team");
    }
}
