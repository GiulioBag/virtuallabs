package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotEnrolledToCourseException extends StudentException {
    public StudentNotEnrolledToCourseException(String serialNumber, String courseName){
        super("Studente: " + serialNumber + " is not enrolled to course " + courseName);
    }
}
