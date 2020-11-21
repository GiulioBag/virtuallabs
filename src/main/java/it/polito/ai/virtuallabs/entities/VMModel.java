package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class VMModel {

    @Id
    private String id;
    private String os;
    private String version;
    private int vcpu;
    private int space;
    private int ram;
    private int activeInstances;
    private int instances;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "name")
    private Course course;

    public void changeCourse(Course course){
        course.changeVMModel(this);
    }

    @PrePersist
    private void ensureId() {
        if (id == null) {
            this.setId(UUID.randomUUID().toString());
        }
    }
}
