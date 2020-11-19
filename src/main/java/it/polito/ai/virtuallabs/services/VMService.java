package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.VMDTO;

import java.security.Principal;
import java.util.List;

public interface VMService {
    // VMS MANAGEMENT

    void vmChangeParam(VMDTO vmdto, Principal principal);
    void changeState(String vmId, String action, Principal principal);
    void switchOnVM(String vmId, Principal principal);
    void switchOffVM(String vmId, Principal principal);
    void deleteVM(String vmId, Principal principal);
    void createVM(VMDTO vmdto, String courseId, Principal principal);
    Byte[] execVM(String vmId, Principal principal);

}
