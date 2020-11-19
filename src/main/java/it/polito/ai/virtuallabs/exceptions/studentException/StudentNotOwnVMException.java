package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotOwnVMException  extends StudentException {
    public StudentNotOwnVMException (String studentId, String vmId){
        super("The student " + studentId + " not owns " + vmId + " vm");
    }
}
