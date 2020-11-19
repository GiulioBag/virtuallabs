package it.polito.ai.virtuallabs.dtos;

import it.polito.ai.virtuallabs.entities.Team;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;


@Data
@EqualsAndHashCode(callSuper = false)
public class TeamDTO extends RepresentationModel<TeamDTO> {
    private String id;
    private String name;
    private boolean active;
    private Timestamp timeout;

    public TeamDTO (Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.active = team.isActive();
        this.timeout = team.getTimeout();
    }

    public TeamDTO () {}

}
