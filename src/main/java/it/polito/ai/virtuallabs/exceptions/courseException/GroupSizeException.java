package it.polito.ai.virtuallabs.exceptions.courseException;

public class GroupSizeException extends CourseException {
    public GroupSizeException() {
        super("Min/Max group size are incorrect");
    }
}
