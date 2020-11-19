package it.polito.ai.virtuallabs.entities;

import it.polito.ai.virtuallabs.dtos.DeliveredPaperDTO;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class DeliveredPaper {
    @Id
    private String id;
    private PaperStatus status;
    private Timestamp deliveredDate;
    private String image;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    // add/remove paper
    public void setPaper(Paper p){
        if(p != null)
            p.getDeliveredPapers().add(this);
        else
            paper.getDeliveredPapers().remove(this);
        paper = p;
    }

    public DeliveredPaper(){}

    public DeliveredPaper(PaperStatus status, long time, String image, Paper paper){
        this.status = status;
        deliveredDate = new Timestamp(time);
        this.image = image;
        this.paper = paper;
    }

    public DeliveredPaper(DeliveredPaperDTO dp){
        id = dp.getId();
        status = dp.getStatus();
        deliveredDate = dp.getDeliveredDate();
        //TODO: salvare l'immagine del DTO e memorizzare nel campo "vmImage" il path
        image = null;
    }

    private void setId(String id){
        this.id = id;
    }

    @PrePersist
    private void ensureId(){
        this.setId(UUID.randomUUID().toString());
    }
}
