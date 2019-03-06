package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;


@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all(@RequestParam() String token) {
        if (service.validateToken(token)) {
            return service.getUsers();
        }
        else {
            throw new AuthenticationException("token invalid");
        }
    }

    @GetMapping("/users/{username}")
    User login(@PathVariable String username, @RequestParam() String pw) {
        return this.service.loginUser(username, pw);
    }


    @PostMapping("/users/{username}")
    @ResponseStatus(HttpStatus.OK)
    String logout(@PathVariable String username, @RequestBody LogoutCredentials cred) {
        return this.service.logoutUser(username, cred.token);
    }
    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }
}


class LogoutCredentials implements Serializable {
    public String token;
}