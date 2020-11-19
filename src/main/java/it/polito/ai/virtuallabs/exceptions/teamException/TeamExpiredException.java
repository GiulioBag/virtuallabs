package it.polito.ai.virtuallabs.exceptions.teamException;

public class TeamExpiredException extends TeamException {
    public TeamExpiredException(String teamName){
        super("No operations can be performed on " + teamName + " team, it is expired");
    }
}
