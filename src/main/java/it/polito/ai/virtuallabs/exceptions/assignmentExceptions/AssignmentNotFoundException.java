package it.polito.ai.virtuallabs.exceptions.assignmentExceptions;

public class AssignmentNotFoundException extends AssignmentException{
    public AssignmentNotFoundException(String s){
        super("Assignment not found with ID: " + s);
    }
}
