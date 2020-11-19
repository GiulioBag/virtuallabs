package it.polito.ai.virtuallabs.exceptions.deliveredPaperException;

public class WrongStutusDeliveredPaperException extends DeliveredPaperException {
    public WrongStutusDeliveredPaperException (){
        super("The paper cannot be updated");
    }

}
