package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Assignment;
import it.polito.ai.virtuallabs.entities.DeliveredPaper;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PaperDTO   {
    private String id;
    private boolean changeable;
    private int score;
}
