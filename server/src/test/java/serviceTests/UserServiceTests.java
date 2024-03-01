package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    AuthDAO authDAO;
    UserDAO userDAO;

    GameDAO gameDAO;
    LoginService loginService;
    LogoutService logoutService;
    RegistrationService registrationService;
    GameService gameService;

    ClearService clearService;

    @BeforeEach
    public void beforeEach() {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO, userDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void shouldGetUser() {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var user = loginService.getUser(username);

        assertEquals(username, user.getUsername());
    }

    @Test
    public void shouldGetNullUserWhenNotExists() {
        final var username = "username";
        final var user = loginService.getUser(username);
        assertNull(user);
    }

    @Test
    public void shouldGetAuth() throws DataAccessException {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var auth = registrationService.createAuth(username);

        final var authData = loginService.getAuth(auth.getAuthToken());

        assertEquals(username, authData.getUsername());
        assertEquals(auth.getAuthToken(), authData.getAuthToken());
    }

    @Test
    public void shouldGetNullAuthWhenNotExists() {
        final var authToken = "authToken";
        final var authResult = loginService.getAuth(authToken);
        assertEquals(null, authResult);
    }

    @Test
    public void shouldCreatUser() {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var userResult = loginService.getUser(username);

        assertEquals(userResult, user1);
    }

    @Test
    public void createUserShouldThrowIllegalArguments() {
        final String username = null;
        final String email = null;
        final String password = null;
        final var user1 = new UserData(username, email, password);

        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createUser(user1);
        });
    }

    @Test
    public void shouldGetUserByAuthToken() throws DataAccessException {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var auth = registrationService.createAuth(username);

        final var userResult = gameService.getUserByAuth(auth.getAuthToken());
        assertEquals(userResult, user1);
    }

    @Test
    public void shouldGetNullUserByAuthTokenWhenNotExists() {
        final var authToken = "authToken";
        final var userResult = gameService.getUserByAuth(authToken);
        assertEquals(null, userResult);
    }

    @Test
    public void shouldCreateAuth() throws DataAccessException {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var auth = registrationService.createAuth(username);

        final var authResult = loginService.getAuth(auth.getAuthToken());

        assertEquals(authResult, auth);
    }

    @Test
    public void shouldThrowDataAccessExceptionWhenUserNotFound() {
        final var username = "username";
        assertThrows(DataAccessException.class, () -> {
            registrationService.createAuth(username);
        });
    }

    @Test
    public void shouldDeleteAuth() throws DataAccessException {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        final var auth = registrationService.createAuth(username);

        logoutService.deleteAuth(auth.getAuthToken());

        final var authResult = loginService.getAuth(auth.getAuthToken());

        assertEquals(null, authResult);
    }

    @Test
    public void shouldNotThrowWhenAuthTokenDoesNotExist() {
        final var authToken = "authToken";
        logoutService.deleteAuth(authToken);
    }


    @Test
    public void shouldGetNullWhenNoAuthByUsername() {
        final var username = "username";
        final var authResult = gameService.getUserByAuth(username);
        assertEquals(null, authResult);
    }


    @Test
    public void shouldDeleteAllUsers() {
        final var username = "username";
        final var email = "email";
        final var password = "password";
        final var user1 = new UserData(username, email, password);

        registrationService.createUser(user1);

        clearService.deleteAllUsers();

        final var userResult = loginService.getUser(username);

        assertEquals(null, userResult);
    }

    @Test
    public void shouldNotThrowWhenNoUsers() {
        clearService.deleteAllUsers();
    }

}