package it.polito.ai.virtuallabs.exceptions.userException;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String serialNUmber) {
        super("The user " + serialNUmber + " is not registered.");
    }
}
