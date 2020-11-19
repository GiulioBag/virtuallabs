package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {

    private String email;
    private String name;
    private String lastName;
    private String serialNumber;
    private Byte[] photo;

    public StudentDTO(Student s){
        email=s.getUser().getEmail();
        name = s.getUser().getName();
        lastName = s.getUser().getLastName();
        serialNumber = s.getUser().getSerialNumber();
        //TODO: dal path recuperare il Byte Array corrispondente
        photo = null;
    }

}
