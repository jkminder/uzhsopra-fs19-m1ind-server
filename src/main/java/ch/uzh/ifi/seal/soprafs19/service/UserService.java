package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserExistingException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import ch.uzh.ifi.seal.soprafs19.exceptions.PasswordNotValidException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User loginUser(String username, String password) {
        User temp = this.userRepository.findByUsername(username);
        if (temp == null) throw new UserNotFoundException(username);
        if (temp.getPassword().equals(password)) {
            temp.setStatus(UserStatus.ONLINE);
            log.debug("User {} logged in!", username);
            return temp;
        }
        else throw new PasswordNotValidException(username);
    }

    public String logoutUser(String username, String token) {
        User temp = this.userRepository.findByUsername(username);
        if (!temp.getToken().equals(token)) {
            throw new AuthenticationException("Token invalid");
        }
        temp.setStatus(UserStatus.OFFLINE);
        return "logout successful!";
    }

    public User createUser(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()) != null) throw new UserExistingException(newUser.getUsername());
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        Calendar today = Calendar.getInstance();
        newUser.setCreationDate(today.getTime());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public Boolean validateToken(String token) {
        return this.userRepository.findByToken(token) != null;
    }
}
