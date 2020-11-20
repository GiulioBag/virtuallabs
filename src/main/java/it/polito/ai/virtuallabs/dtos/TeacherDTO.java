package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    private String email;
    private String name;
    private String lastName;
    private String serialNumber;
    private Byte[] photo;

}
