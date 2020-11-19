package it.polito.ai.virtuallabs.exceptions.teacherExceptions;

public class TeacherNotFoundException extends TeacherException {
    public TeacherNotFoundException(String t) { super ("Teacher not found with ID: " + t); }
}
