package it.polito.ai.virtuallabs.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class VMModel {

    @Id
    @GeneratedValue
    private String id;
    private String OS;
    private String version;
    private int vcpu;
    private int space;
    private int ram;
    private int activeInstances;
    private int instances;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "name")
    private Course course;



}
