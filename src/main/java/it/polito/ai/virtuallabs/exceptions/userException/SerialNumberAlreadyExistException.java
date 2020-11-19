package it.polito.ai.virtuallabs.exceptions.userException;

public class SerialNumberAlreadyExistException  extends UserException {
    public SerialNumberAlreadyExistException() {
        super("The entered serial number is already used.");
    }
}
