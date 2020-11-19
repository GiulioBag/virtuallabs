package it.polito.ai.virtuallabs.exceptions.paperExceptions;

public class PaperNotChangeableException extends PaperException {
    public  PaperNotChangeableException(String paperId){
        super("It is no longer possible to modify paper " + paperId);
    }
}
