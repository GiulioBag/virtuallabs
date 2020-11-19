package it.polito.ai.virtuallabs.exceptions.vmModelExceptions;

public class VMModelExcessiveLimitsException extends VMModelException{
    public VMModelExcessiveLimitsException() {
        super("Limitazioni non applicabili al corso perché eccessive. Almeno un team sta sfruttando più risorse di quelle indicate dal nuovo modello.");
    }
}
