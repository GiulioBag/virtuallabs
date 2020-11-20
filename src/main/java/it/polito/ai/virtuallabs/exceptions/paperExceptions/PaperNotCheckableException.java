package it.polito.ai.virtuallabs.exceptions.paperExceptions;

public class PaperNotCheckableException extends PaperException{
    public PaperNotCheckableException(){ super("Peper non checkable because it is non delivered yet"); }
}
