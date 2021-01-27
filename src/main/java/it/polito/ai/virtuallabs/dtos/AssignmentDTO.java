package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private String id;
    private String name;
    private Timestamp releaseDate;
    private Timestamp expireDate;
    private byte[] content;

}
