package it.polito.ai.virtuallabs.exceptions.vmException;

public class NotAllowedActionException  extends VmException {
    public NotAllowedActionException (String action) {
        super("It is not possible to perform the action: " + action);
    }
}
