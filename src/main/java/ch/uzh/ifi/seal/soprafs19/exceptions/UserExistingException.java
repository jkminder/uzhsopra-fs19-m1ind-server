package ch.uzh.ifi.seal.soprafs19.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserExistingException extends RuntimeException {
    public UserExistingException(String name) {
        super("User " + name + " is already used!");
    }
}