package it.polito.ai.virtuallabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluatedPaperDTO {
    private String comment;
    private int score;
    private boolean accepted;
    private byte[] img;
}
