package dataAccessTests;

import com.mysql.cj.log.Log;
import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import javax.xml.crypto.Data;

import static dataAccess.DatabaseManager.createDatabase;

public class DAOtests {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static ClearService clearService;

    @BeforeEach
    public void beforeEach() throws DataAccessException {
        try {
            createDatabase();
        } catch(Exception e) {
            System.out.println("Db not created");
        }

        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameService gameService = new GameService(gameDAO, authDAO, userDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        RegistrationService registrationService = new RegistrationService(userDAO, authDAO);

        clearService.clearAll();
    }

    @Test
    public void testGetAuth() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";
        final var authData = new AuthData(authToken, username);

        authDAO.createAuth(username, authToken);
        final var result = authDAO.getAuth(authToken);Assertions.assertEquals(result, authData);
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException {
        final var authToken = "authtoken";
        final var result = authDAO.getAuth(authToken);
        Assertions.assertNull(result);
    }

    @Test
    public void testCreateAuth() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";
        final var authData = new AuthData(authToken, username);

        authDAO.createAuth(username, authToken);
        final var result = authDAO.getAuth(authToken);

        Assertions.assertEquals(result, authData);
    }

    @Test
    public void testCreateAuthInvalid() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null, null);
        });
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";

        authDAO.createAuth(username, authToken);
        authDAO.deleteAuth(authToken);

        final var result = authDAO.getAuth(authToken);
        Assertions.assertNull(result);
    }

    @Test
    public void testDeleteNonExistentAuth() throws DataAccessException {
        final var authToken = "authtoken";

        authDAO.deleteAuth(authToken);

        final var result = authDAO.getAuth(authToken);
        Assertions.assertNull(result);
    }

    @Test
    public void testDeleteAllAuth() throws DataAccessException {
        final var username = "username";
        final var username2 = "username2";
        final var authToken = "authtoken";
        final var authToken2 = "authtoken2";

        authDAO.createAuth(username, authToken);
        authDAO.createAuth(username2, authToken2);
        authDAO.deleteAllAuth();

        final var result = authDAO.getAuth(authToken);
        final var result2 = authDAO.getAuth(authToken2);

        Assertions.assertNull(result);
        Assertions.assertNull(result2);
    }

    @Test
    public void testCreateUser() throws DataAccessException {
        var username = "username";
        var password = "password1";
        var email = "email@email.com";
        var newUser = new UserData(username, password, email);
        var insertedUser = userDAO.createUser(newUser);

        Assertions.assertEquals(newUser, insertedUser);
    }

    @Test
    public void testCreateInvalidUser() throws DataAccessException {
        final var username = "username";
        final var email = "email";
        final var password = "password";

        final var user1 = new UserData(null, email, password);
        final var user2 = new UserData(username, null, password);
        final var user3 = new UserData(username, email, null);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user1));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user3));
    }

    @Test
    public void testGetUser() throws DataAccessException {
        final var username = "username90";
        final var email = "email90";
        final var password = "password90";

        final var user = new UserData(username, password, email);
        userDAO.createUser(user);
        final var result = userDAO.getUser(username);
        Assertions.assertEquals(result, user);
    }


    @Test
    public void testDeleteAllUsers() throws DataAccessException {
        final var username = "username";
        final var username2 = "username2";
        final var password = "password";
        final var password2 = "password2";
        final var email = "email1@email.com";
        final var email2 = "email2@email.com";

        userDAO.createUser(new UserData(username, password, email));
        userDAO.createUser(new UserData(username2, password2, email2));
        userDAO.deleteAllUsers();

        final var result = userDAO.getUser(username);
        final var result2 = userDAO.getUser(username2);

        Assertions.assertNull(result);
        Assertions.assertNull(result2);
    }

}
