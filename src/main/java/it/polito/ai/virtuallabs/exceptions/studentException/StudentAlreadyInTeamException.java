package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentAlreadyInTeamException extends StudentException {
    public StudentAlreadyInTeamException(String serialNumner, String courseName){
        super("The student " + serialNumner + " is already present in a team of the course " + courseName);
    }
}
