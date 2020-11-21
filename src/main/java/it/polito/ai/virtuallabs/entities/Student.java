package it.polito.ai.virtuallabs.entities;

import it.polito.ai.virtuallabs.dtos.StudentDTO;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Student {
    @Id
    private String id;

   @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
   @JoinTable(name = "student_team", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_proposedTeam", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> proposedTeams = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_vm", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "vm_id"))
    private List<VM> vms = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Paper> papers = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "serialNumber")
    private User user;

    public Student(User user){
        this.user = user;
    }
    public Student(){super();};

    // add/remove courses from list
    public void addCourse(Course course){
        courses.add(course);
        course.getStudents().add(this);
    }

    public void removeCourse(Course course){
        course.getStudents().remove(this);
        courses.remove(course);
    }

    // add/remove teams from list
    public void addTeam(Team team){
        teams.add(team);
        team.getStudents().add(this);
    }

    public void removeTeam(Team team){
        team.getStudents().remove(this);
        teams.remove(team);
    }

    public void addProposedTeam(Team team){
        proposedTeams.add(team);
        team.getWaitingStudents().add(this);
    }

    public void removeProposedTeam(Team team){
        team.getWaitingStudents().remove(this);
        proposedTeams.remove(team);
    }

    // add/remove papers from list
    public void addPaper(Paper paper) {
        paper.setStudent(this);
    }

    public void removePaper(Paper paper) {
        paper.setStudent(null);
    }


    @PrePersist
    private void ensureId(){
        if(this.id == null)
            this.setId(UUID.randomUUID().toString());
    }

}
