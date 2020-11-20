package it.polito.ai.virtuallabs.exceptions.vmException;

public class VmOffException extends VmException{
    public VmOffException(){ super("Cannot exec VM because it is turned OFF");}
}
