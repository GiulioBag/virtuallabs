package it.polito.ai.virtuallabs.exceptions.vmException;

public class VmParameterException extends VmException {
    public VmParameterException(String param) {
        super("The value of the " + param + " parameter is incorrect");
    }
}
