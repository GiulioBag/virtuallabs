package it.polito.ai.virtuallabs.exceptions.teamException;

public class TeamNotActivedException extends TeamException {
    public TeamNotActivedException(String teamName){
        super("No operations can be performed on " + teamName + " team as it has not been activated");
    }
}
