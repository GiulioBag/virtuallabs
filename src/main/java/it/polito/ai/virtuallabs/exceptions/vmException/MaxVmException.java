package it.polito.ai.virtuallabs.exceptions.vmException;

public class MaxVmException extends VmException {
    public MaxVmException () {
        super("it is not possible to crete a new vm, the maximum number of vm has already been reached.");
    }
}
