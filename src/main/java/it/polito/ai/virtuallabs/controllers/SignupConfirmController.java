package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.exceptions.confirmTokenException.ConfirmTokenExpiredException;
import it.polito.ai.virtuallabs.exceptions.confirmTokenException.ConfirmTokenNotFoundException;
import it.polito.ai.virtuallabs.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/API/users")
public class SignupConfirmController {

    @Autowired
    UserService userService;

    @GetMapping("/confirm/{tokenId}")
    @ResponseStatus(HttpStatus.OK)
    public String confirmRegistration(@PathVariable(name = "tokenId") String tokenID) {
        try {
            userService.confirmRegistration(tokenID);
            return "signupConfirm";
        } catch (ConfirmTokenNotFoundException | UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConfirmTokenExpiredException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
