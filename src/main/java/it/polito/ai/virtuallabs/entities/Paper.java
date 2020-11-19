package it.polito.ai.virtuallabs.entities;

import it.polito.ai.virtuallabs.enums.PaperStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Paper {
    @Id
    @GeneratedValue
    private String id;
    private boolean changeable;
    private int score;

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

}
