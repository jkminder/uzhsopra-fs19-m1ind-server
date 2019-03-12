package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
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

    //FOR DEBUGGING PURPOSES
    @GetMapping("/debug/users")
    Iterable<User> all() {
        return service.getUsers();
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

    @GetMapping("/users/{id}")
    User one(@PathVariable String id,  @RequestParam() String token) {
        if (service.validateToken(token)) {
            return service.getUser(id);
        }
        else {
            throw new AuthenticationException("token invalid");
        }
    }

    //dto -> for updates
    @CrossOrigin
    @PutMapping("/users/{id}")
    User update(@PathVariable String id,  @RequestBody User updateUser, @RequestParam() String token) {
        return this.service.updateUser(id, updateUser, token);
    }

    @PostMapping("/users/login")
    AuthorizationCredentials login(@RequestBody LoginCredentials cred) {
        AuthorizationCredentials acred = new AuthorizationCredentials();
        User local = this.service.loginUser(cred.username, cred.password);
        acred.token = local.getToken();
        acred.id = local.getId();
        return acred;
    }

    @PostMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    String logout(@RequestBody AuthorizationCredentials cred) {
        return this.service.logoutUser(cred.token);
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }
}


class AuthorizationCredentials implements Serializable {
    public String token;
    public Long id;
}

class LoginCredentials implements Serializable {
    public String username;
    public String password;
}