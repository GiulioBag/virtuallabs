package it.polito.ai.virtuallabs.exceptions.vmException;

public class VmNotFoundException extends VmException {
    public VmNotFoundException(String vmId){
        super("Vm " + vmId + " not found");
    }
}
