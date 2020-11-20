package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.VM;
import it.polito.ai.virtuallabs.enums.VmState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VMDTO {

    private String id;
    private int vcpu;
    private int space;
    private int ram;
    private VmState state;
    private Byte[] vmImage;

}
