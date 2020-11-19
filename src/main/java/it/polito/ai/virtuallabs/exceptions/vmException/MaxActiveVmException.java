package it.polito.ai.virtuallabs.exceptions.vmException;

public class MaxActiveVmException extends VmException {
    public MaxActiveVmException (String vmId) {
        super("it is not possible to switch on the vm" + vmId + " , the maximum number of vm switched on at the same" +
                " time has already been reached.");
    }
}
