package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.AuthenticationRequestDTO;
import it.polito.ai.virtuallabs.dtos.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    UserDTO getUser(String userId);
    void addUser (UserDTO user);
    void confirmRegistration(String tokeID);
    ResponseEntity<Map<Object, Object>> signin (AuthenticationRequestDTO data);
    void deleteToken(String tokenID);
}
