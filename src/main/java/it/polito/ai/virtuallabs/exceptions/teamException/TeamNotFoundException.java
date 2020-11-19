package it.polito.ai.virtuallabs.exceptions.teamException;

public class TeamNotFoundException extends TeamException {
    public TeamNotFoundException (String teamName){
        super(teamName + " is not a team");
    }
}
