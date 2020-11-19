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
    private Byte[] image;

    public DeliveredPaperDTO(DeliveredPaper dp){
        id = dp.getId();
        status = dp.getStatus();
        deliveredDate = dp.getDeliveredDate();
        //TODO: dal path recuperare il Byte Array corrispondente
        image = null;
    }

}
