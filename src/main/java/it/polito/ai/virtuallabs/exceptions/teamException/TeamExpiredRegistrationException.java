package it.polito.ai.virtuallabs.exceptions.teamException;

public class TeamExpiredRegistrationException extends TeamException {
    public TeamExpiredRegistrationException(String teamName){
        super("You can not register to " + teamName + ", your invite is expired");
    }
}
