package it.polito.ai.virtuallabs.exceptions.courseException;

public class GroupSizeException extends CourseException{
    public GroupSizeException(){ super("Minimum group size can not be greater than maximum group size");}
}
