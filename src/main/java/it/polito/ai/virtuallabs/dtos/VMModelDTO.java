package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)

public class VMModelDTO {

    private String id;
    private String OS;
    private String version;
    private int vcpu;
    private int space;
    private int ram;
    private int activeInstances;
    private int instances;

}
