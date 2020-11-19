package it.polito.ai.virtuallabs.entities;

import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.enums.VmState;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class VM {

    @Id
    private String id;
    private int vcpu;
    private int space;
    private int ram;
    private VmState state;
    private String vmImage;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany(mappedBy = "vms")
    private List<Student> owners = new ArrayList<>();

    public VM(VMDTO vmdto){
        id = vmdto.getId();
        vcpu = vmdto.getVcpu();
        space = vmdto.getSpace();
        ram = vmdto.getRam();
        state = vmdto.getState();
        //TODO: salvare l'immagine del DTO e memorizzare nel campo "vmImage" il path
        vmImage = null;
    }

    // add/remove team
    public void setTeam(Team t){
        if(t != null)
            t.getVMs().add(this);
        else
            team.getVMs().remove(this);
        team = t;
    }

    @PrePersist
    private void ensureId(){
        this.setId(UUID.randomUUID().toString());
    }

}
