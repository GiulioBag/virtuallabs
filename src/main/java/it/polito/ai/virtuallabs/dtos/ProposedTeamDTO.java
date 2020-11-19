package it.polito.ai.virtuallabs.dtos;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProposedTeamDTO {

    private String name;
    private List<String> studentIds;
    private Date timeout;
}
