package it.polito.ai.virtuallabs.exceptions.assignmentExceptions;

public class ExpiredAssignmentException extends AssignmentException {
    public ExpiredAssignmentException (String assId){
        super(assId + " assignment is expired");
    }
}
