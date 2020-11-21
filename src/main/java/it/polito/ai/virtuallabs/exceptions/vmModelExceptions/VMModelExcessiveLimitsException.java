package it.polito.ai.virtuallabs.exceptions.vmModelExceptions;

public class VMModelExcessiveLimitsException extends VMModelException{
    public VMModelExcessiveLimitsException() {
        super("Limitations not applicable to the course because they are excessive. At least one team is leveraging more resources than indicated by the new model");
    }
}
