package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class CourseDTO  extends RepresentationModel<CourseDTO> {

    private String name;
    private String acronym;
    private boolean status;
    private int minGroupSize;
    private int maxGroupSize;

}
