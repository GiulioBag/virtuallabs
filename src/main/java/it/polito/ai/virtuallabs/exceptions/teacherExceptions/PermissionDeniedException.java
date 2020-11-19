package it.polito.ai.virtuallabs.exceptions.teacherExceptions;

public class PermissionDeniedException extends TeacherException {
    public PermissionDeniedException() { super ("Permission denied for this user to perform this action."); }
}
