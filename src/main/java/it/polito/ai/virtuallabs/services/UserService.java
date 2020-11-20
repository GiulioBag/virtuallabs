package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.AuthenticationRequestDTO;
import it.polito.ai.virtuallabs.dtos.UserDTO;
import it.polito.ai.virtuallabs.entities.User;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public interface UserService {

    UserDTO getUser(String userId) throws IOException;
    void addUser (UserDTO user) throws IOException;
    void confirmRegistration(String tokeID);
    ResponseEntity<Map<Object, Object>> signin (AuthenticationRequestDTO data);
    void deleteToken(String tokenID);

}
