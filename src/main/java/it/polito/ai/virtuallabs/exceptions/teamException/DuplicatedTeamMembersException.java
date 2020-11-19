package it.polito.ai.virtuallabs.exceptions.teamException;

public class DuplicatedTeamMembersException extends TeamException {
    public DuplicatedTeamMembersException(){
        super("There are duplicated students in the team.");
    }
}
