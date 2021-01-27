package it.polito.ai.virtuallabs.entities;

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
    private String comment;

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

    public DeliveredPaper(PaperStatus status, long time, Paper paper){
        this.status = status;
        deliveredDate = new Timestamp(time);
        this.paper = paper;
    }

    @PrePersist
    private void ensureId(){
        if(this.getId() == null)
            this.setId(UUID.randomUUID().toString());
    }
}
