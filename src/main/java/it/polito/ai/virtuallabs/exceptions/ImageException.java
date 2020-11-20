package it.polito.ai.virtuallabs.exceptions;

public class ImageException extends MyException {
    public ImageException(String mess){
        super("Error during upload image: " + mess);
    }
}
