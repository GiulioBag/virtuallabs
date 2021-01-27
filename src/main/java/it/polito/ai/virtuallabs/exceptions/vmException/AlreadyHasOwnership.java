package it.polito.ai.virtuallabs.exceptions.vmException;

public class AlreadyHasOwnership extends VmException {
    public AlreadyHasOwnership(String vmId, String studentId) {
        super("The student " + studentId + "already owns the VM with id " + vmId);
    }
}
