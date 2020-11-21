package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {
    @Id
    private String name;
    private String acronym;
    private boolean status;
    private int minGroupSize;
    private int maxGroupSize;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Teacher> owners = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments = new ArrayList<>();

    @OneToOne(mappedBy = "course")
    private VMModel vmModel = null;

    // add/remove students from list
    public void addStudent(Student student){
        student.addCourse(this);
    }

    public void removeStudent(Student student){
        student.removeCourse(this);
    }

    // add/remove teachers from list
    public void addTeacher(Teacher teacher){
        teacher.addCourse(this);
    }

    public void removeTeacher(Teacher teacher){
        teacher.removeCourse(this);
    }

    // add/remove teams from list
    public void addTeam(Team team){
        team.setCourse(this);
    }

    public void removeTeam(Team team){
        team.setCourse(null);
    }

    // add/remove assignments from list
    public void addAssignment(Assignment assignment){
        assignment.setCourse(this);
    }

    public void removeAssignment(Assignment assignment){
        assignment.setCourse(null);
    }

    public void changeVMModel(VMModel vmModel){
        this.vmModel = vmModel;
        vmModel.setCourse(this);
    }

}
