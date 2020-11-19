package it.polito.ai.virtuallabs.entities;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
public class ConfirmToken {

    public ConfirmToken() {
    }


    public ConfirmToken(String id, String serialNumber, Timestamp timestamp) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.expiryDate = timestamp;
    }


    @Id
    private String id;
    private String serialNumber;
    private Timestamp expiryDate;
}
