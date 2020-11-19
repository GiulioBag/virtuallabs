package it.polito.ai.virtuallabs.exceptions.userException;

public class BadFieldValueException extends UserException {
    public BadFieldValueException (String field){
        super("The value of the " + field + " field is incorrect");
    }
}
