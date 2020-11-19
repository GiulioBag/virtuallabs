package it.polito.ai.virtuallabs.exceptions.deliveredPaperException;

public class MissFiledDeliveredPaperException extends DeliveredPaperException {
    public MissFiledDeliveredPaperException(){
        super("Fields are missing on the deliveredPaper sent");
    }
}
