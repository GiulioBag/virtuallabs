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

    public TeacherDTO(Teacher t){
        email = t.getUser().getEmail();
        name = t.getUser().getName();
        lastName = t.getUser().getLastName();
        serialNumber = t.getUser().getSerialNumber();
        //TODO: dal path recuperare il Byte Array corrispondente
        photo = null;
    }
}
