package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class PaperDTO extends RepresentationModel<PaperDTO> {
    private String id;
    private boolean changeable;
    private int score;
}
