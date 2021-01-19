package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.enums.VmState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class VMDTO extends RepresentationModel<VMDTO> {

    private String id;
    private int vcpu;
    private int space;
    private int ram;
    private VmState state;
    private Byte[] vmImage;
}
