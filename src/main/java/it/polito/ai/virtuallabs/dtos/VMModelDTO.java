package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)

public class VMModelDTO extends RepresentationModel<VMModelDTO> {

    private String id;
    private String os;
    private String version;
    private int vcpu;
    private int space;
    private int ram;
    private int activeInstances;
    private int instances;

}
