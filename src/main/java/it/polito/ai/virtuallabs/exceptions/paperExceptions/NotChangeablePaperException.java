package it.polito.ai.virtuallabs.exceptions.paperExceptions;

public class NotChangeablePaperException extends PaperException {
    public NotChangeablePaperException(String paperId){
        super(paperId + " paper is not changeable anymore");
    }
}
