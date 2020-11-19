package it.polito.ai.virtuallabs.exceptions.vmException;

public class VmCourseNotActive extends VmException {
    public VmCourseNotActive(String vmId){
        super("The course associated with the vm " + vmId + " team is not active");
    }
}
