package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentHasNotPaper extends StudentException {
    public StudentHasNotPaper(String studentID, String paperID) {
        super(studentID + " does not own " + paperID + " paper");
    }
}


