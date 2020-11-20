package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.DeliveredPaper;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeliveredPaperDTO   {

    private String id;
    private PaperStatus status;
    private Timestamp deliveredDate;
    private byte[] image;

}
