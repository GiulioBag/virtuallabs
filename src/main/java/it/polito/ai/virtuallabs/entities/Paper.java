package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Paper {
    @Id
    private String id;
    private boolean changeable;
    private int score = -1;


    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "paper")
    private List<DeliveredPaper> deliveredPapers = new ArrayList<>();

    // add/remove assignment
    public void setAssignment(Assignment a){
        if(a != null)
            a.getPapers().add(this);
        else
            assignment.getPapers().remove(this);
        assignment = a;
    }

    // add/remove student
    public void setStudent(Student s){
        if(s != null)
            s.getPapers().add(this);
        else
            student.getPapers().remove(this);
        student = s;
    }

    // add/remove delivered papers from list
    public void addDeliveredPaper(DeliveredPaper deliveredPaper) {
        deliveredPaper.setPaper(this);
    }

    public void removeDeliveredPaper(DeliveredPaper deliveredPaper) {
        deliveredPaper.setPaper(null);
    }

    @PrePersist
    private void ensureId() {
        if (id == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }

}
