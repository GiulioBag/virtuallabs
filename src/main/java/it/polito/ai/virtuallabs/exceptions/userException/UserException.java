package it.polito.ai.virtuallabs.exceptions.userException;

import it.polito.ai.virtuallabs.exceptions.MyException;

public class UserException extends MyException {
    public UserException(String mess) {
        super(mess);
    }
}
