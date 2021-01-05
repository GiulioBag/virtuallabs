package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentAlreadyEnrolled extends StudentException {
    public StudentAlreadyEnrolled(String serialNumner, String courseName) {
        super("The student " + serialNumner + " is already enrolled on " + courseName);
    }
}
