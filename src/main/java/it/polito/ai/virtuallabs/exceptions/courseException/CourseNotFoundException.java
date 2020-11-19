package it.polito.ai.virtuallabs.exceptions.courseException;

public class CourseNotFoundException extends CourseException {
    public CourseNotFoundException(String courseName) { super("Course not found with name: " + courseName); }
}
