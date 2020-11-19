package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Assignment;
import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.Paper;
import it.polito.ai.virtuallabs.entities.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssignmentDTO {

    private String id;
    private String name;
    private Timestamp releaseDate;
    private Timestamp expireDate;
    private Byte[] content;

    public AssignmentDTO(Assignment assignment){
        id = assignment.getId();
        name = assignment.getName();
        releaseDate = assignment.getReleaseDate();
        expireDate = assignment.getExpireDate();
        //TODO: dal path recuperare il Byte Array corrispondente
        content = null;
    }

}
