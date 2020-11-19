package it.polito.ai.virtuallabs.exceptions.teamException;

public class TeamAlreadyExistException extends TeamException {
    public TeamAlreadyExistException(String teamName, String courseName){
        super(teamName + " is already present in course " + courseName);
    }
}
