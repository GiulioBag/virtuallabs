package it.polito.ai.virtuallabs.exceptions.courseException;

import it.polito.ai.virtuallabs.exceptions.MyException;

public class CourseException extends MyException {
    CourseException(String mess){
        super((mess));
    }
}
