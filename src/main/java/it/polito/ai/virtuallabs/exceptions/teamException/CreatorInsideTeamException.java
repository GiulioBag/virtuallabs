package it.polito.ai.virtuallabs.exceptions.teamException;

public class CreatorInsideTeamException extends TeamException {
    public CreatorInsideTeamException(){
        super("The creator of the team cannot be in the invited student list");
    }
}
