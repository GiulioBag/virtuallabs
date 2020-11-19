package it.polito.ai.virtuallabs.exceptions.paperExceptions;

public class PaperNotFoundException extends PaperException{
    public PaperNotFoundException(String s){ super("Paper not found with ID: " + s) ;}
}
