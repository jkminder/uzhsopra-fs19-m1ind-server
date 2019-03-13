package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.PasswordNotValidException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserExistingException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Iterator;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");

        User createdUser = userService.createUser(testUser);

        Assert.assertNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByUsername(createdUser.getUsername()));
    }

    @Test
    public void getUsers() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        Iterable<User> users = userService.getUsers();
        Iterator<User> iter = users.iterator();
        Assert.assertEquals(iter.next(), testUser);
    }

    @Test
    public void loginValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        User local = userService.loginUser("testUsername", "password1234");
        Assert.assertEquals(local, testUser);
        Assert.assertEquals(local.getStatus(),UserStatus.ONLINE);
        Assert.assertNotNull(local.getToken());
    }


    @Test(expected = UserNotFoundException.class)
    public void loginInvalidUsername() {
        userRepository.deleteAll();
        userService.loginUser("someUser", "password1234");
    }

    @Test(expected = PasswordNotValidException.class)
    public void loginInvalidPassword() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "wrongpassword");
    }

    @Test
    public void logoutValidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "password1234");
        userService.logoutUser(userRepository.findByUsername("testUsername").getToken());
        Assert.assertNull(userRepository.findByUsername("testUsername").getToken());
        Assert.assertEquals(userRepository.findByUsername("testUsername").getStatus(), UserStatus.OFFLINE);
    }


    @Test(expected = AuthenticationException.class)
    public void logoutInvalidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "password1234");
        userService.logoutUser("somerandomtokenbutforsurenottherightoneorthiswouldbecrazy");
    }

    @Test
    public void getValidUserId() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        User created = userService.createUser(testUser);

        Assert.assertEquals(userService.getUser(created.getId().toString()), created);
    }

    @Test(expected = UserNotFoundException.class)
    public void getInvalidUserId() {
        userRepository.deleteAll();
        userService.getUser("1");
    }

    @Test
    public void validateInvalidToken() {
        userRepository.deleteAll();
        Assert.assertFalse(userService.validateToken("somerandomtokenbutforsurenottherightoneorthiswouldbecrazy"));
    }

    @Test
    public void validateValidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);
        User local = userService.loginUser("testUsername", "password1234");

        Assert.assertTrue(userService.validateToken(local.getToken()));
    }

    @Test
    public void updateValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);
        User local = userService.loginUser("testUsername", "password1234");

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("testUsernameUpdate");
        updatedUser.setBirthDay("somedateinmilisecondsUpdate");
        updatedUser.setPassword("password1234");

        userService.updateUser(local.getId().toString(), updatedUser, local.getToken());

        local = userRepository.findById(local.getId()).orElse(null);
        Assert.assertEquals(local.getUsername(), updatedUser.getUsername());
        Assert.assertEquals(local.getName(), updatedUser.getName());
        Assert.assertEquals(local.getBirthDay(), updatedUser.getBirthDay());
    }

    @Test(expected = AuthenticationException.class)
    public void updateUserInvalidToken() {
        userRepository.deleteAll();
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        User other = new User();
        other.setName("OtherName");
        other.setUsername("OtherUsername");
        other.setBirthDay("somedateinmiliseconds");
        other.setPassword("password1234");
        userService.createUser(other);

        User local = userService.loginUser("OtherUsername", "password1234");

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("testUsernameUpdate");
        updatedUser.setBirthDay("somedateinmilisecondsUpdate");
        updatedUser.setPassword("password1234");

        userService.updateUser(userRepository.findByUsername("testUsername").getId().toString(), updatedUser, local.getToken());
    }

    @Test(expected = UserExistingException.class)
    public void updateUserUsernameExisting() {
        userRepository.deleteAll();
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay("somedateinmiliseconds");
        testUser.setPassword("password1234");
        userService.createUser(testUser);

        User other = new User();
        other.setName("OtherName");
        other.setUsername("OtherUsername");
        other.setBirthDay("somedateinmiliseconds");
        other.setPassword("password1234");
        userService.createUser(other);

        User local = userService.loginUser("testUsername", "password1234");

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("OtherUsername");
        updatedUser.setBirthDay("somedateinmilisecondsUpdate");
        updatedUser.setPassword("password1234");

        userService.updateUser(userRepository.findByUsername("testUsername").getId().toString(), updatedUser, local.getToken());
    }

}

