package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentAlreadyAcceptedTeamException extends StudentException {
    public  StudentAlreadyAcceptedTeamException (String studentID, String teamId){
        super(studentID + " student already accepted " + teamId + " team invitation");
    }
}
