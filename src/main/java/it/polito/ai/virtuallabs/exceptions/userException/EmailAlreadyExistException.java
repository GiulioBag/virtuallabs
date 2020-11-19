package it.polito.ai.virtuallabs.exceptions.userException;

public class EmailAlreadyExistException extends UserException {
    public EmailAlreadyExistException() {
        super("The entered email is already used.");
    }
}
