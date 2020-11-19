package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Teacher {

    @Id
    String id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "serialNumber")
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "teacher_course", joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<Assignment> assignments = new ArrayList<>();

    public Teacher(){}
    public Teacher (User user){
        this.user = user;
        this.id = user.getSerialNumber();
    }

    // add/remove courses from list
    public void addCourse(Course course){
        courses.add(course);
        course.getOwners().add(this);
    }

    public void removeCourse(Course course){
        course.getOwners().remove(this);
        courses.remove(course);
    }

    // add/remove assignments from list
    public void addAssignment(Assignment assignment) {
        assignment.setCreator(this);
    }

    public void removeAssignment(Assignment assignment) {
        assignment.setCreator(null);
    }
}
