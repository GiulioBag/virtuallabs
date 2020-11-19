package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotFoundException extends StudentException {
    public StudentNotFoundException(String id){super(id + " student does not exist!");}

}
