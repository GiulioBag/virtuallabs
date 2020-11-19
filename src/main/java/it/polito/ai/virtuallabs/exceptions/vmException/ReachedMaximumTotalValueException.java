package it.polito.ai.virtuallabs.exceptions.vmException;

public class ReachedMaximumTotalValueException  extends VmException {
    public ReachedMaximumTotalValueException (String param){
        super("The sum of the " + param + " resource is greater than allowed");
    }
}
