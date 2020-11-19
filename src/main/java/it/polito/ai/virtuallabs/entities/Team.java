package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue
    private String id;
    private String name;
    private boolean active = false;
    private Timestamp timeout;

    @ManyToOne
    @JoinColumn(name = "course_name")
    private Course course;

    @ManyToMany(mappedBy = "teams")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<VM> VMs = new ArrayList<>();

    @ManyToMany(mappedBy = "proposedTeams")
    private List<Student> waitingStudents = new ArrayList<>();

    // add/remove students from list
    public void addStudent(Student student){
        student.addTeam(this);
    }

    public void removeStudent(Student student){
        student.removeTeam(this);
    }

    public void addWaitingStudent(Student student){
        student.addProposedTeam(this);
    }

    public void removeWaitingStudent(Student student){
        student.removeProposedTeam(this);
    }

    // add/remove course
    public void setCourse(Course c){
        if(c != null)
            c.getTeams().add(this);
        else
            course.getTeams().remove(this);
        course = c;
    }

    // add/remove VMs from list
    public void addVM(VM vm){
        vm.setTeam(this);
    }

    public void removeVM(VM vm){
        vm.setTeam(null);
    }

    @PrePersist
    private void ensureId(){
        this.setId(UUID.randomUUID().toString());
    }

}
