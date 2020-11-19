package it.polito.ai.virtuallabs.exceptions.confirmTokenException;

public class ConfirmTokenNotFoundException extends ConfirmTokenException {
    public ConfirmTokenNotFoundException (){
        super("The token you tried to log in with is not present on the system.");
    }
}
