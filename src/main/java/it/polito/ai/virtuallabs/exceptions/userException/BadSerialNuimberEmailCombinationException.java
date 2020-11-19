package it.polito.ai.virtuallabs.exceptions.userException;

public class BadSerialNuimberEmailCombinationException extends UserException {
    public BadSerialNuimberEmailCombinationException() {
        super("Wrong serial number/email combination");
    }

}
