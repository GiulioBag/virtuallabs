package it.polito.ai.virtuallabs.exceptions.vmModelExceptions;

public class VMModelNotDefinedException extends VMModelException {
    public VMModelNotDefinedException(String courseId) {
        super(courseId + " cource has not a VMOdel, please define it before try to insert a VM");
    }
}
