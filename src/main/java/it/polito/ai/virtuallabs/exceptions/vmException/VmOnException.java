package it.polito.ai.virtuallabs.exceptions.vmException;

public class VmOnException extends VmException {
    public VmOnException() {
        super("Cannot change VM parameters while powered on.");
    }
}
