package it.polito.ai.virtuallabs.entities;

import it.polito.ai.virtuallabs.dtos.AssignmentDTO;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Assignment {

    @Id
    @GeneratedValue
    private String id;
    private String name;
    private Timestamp releaseDate;
    private Timestamp expireDate;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher creator;

    @ManyToOne
    @JoinColumn(name = "course_name")
    private Course course;

    @OneToMany(mappedBy = "assignment")
    private List<Paper> papers = new ArrayList<>();

    // add/remove creator
    public void setCreator(Teacher t){
        if(t != null)
            t.getAssignments().add(this);
        else
            creator.getAssignments().remove(this);
        creator = t;
    }

    // add/remove course
    public void setCourse(Course c){
        if(c != null)
            c.getAssignments().add(this);
        else
            course.getAssignments().remove(this);
        course = c;
    }

    // add/remove papers from list
    public void addPaper(Paper paper) {
        paper.setAssignment(this);
    }

    public void removePaper(Paper paper) {
        paper.setAssignment(null);
    }
}
