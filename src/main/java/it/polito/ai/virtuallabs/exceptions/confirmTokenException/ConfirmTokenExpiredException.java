package it.polito.ai.virtuallabs.exceptions.confirmTokenException;

public class ConfirmTokenExpiredException  extends ConfirmTokenException {
    public ConfirmTokenExpiredException () {
        super("The token you tried to log in with has expired");
    }
}
