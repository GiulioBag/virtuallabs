package it.polito.ai.virtuallabs.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
public class JwtBlacklist {
    @Id
    private String token;
    private Date date;


    public JwtBlacklist() {

    }
}
