package it.polito.ai.virtuallabs.dtos;

import com.sun.istack.NotNull;
import it.polito.ai.virtuallabs.entities.User;
import lombok.Data;

@Data
public class UserDTO {

    private String serialNumber;
    private String password;
    private String email;
    private String name;
    private String lastName;
    private Byte [] photo;


    public UserDTO(User user) {
        this.serialNumber = user.getSerialNumber();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.name = user.getName();
        this.lastName = user.getLastName();

        //TODO: dal path recuperare il Byte Array corrispondente
        this.photo = null;
    }

    public UserDTO(){}
}
