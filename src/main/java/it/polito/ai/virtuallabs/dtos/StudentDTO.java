package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    private String id;
    private String email;
    private String name;
    private String lastName;
    private String serialNumber;
    private Byte[] photo;

}
