package dataAccessTests;

import com.mysql.cj.log.Log;
import dataAccess.*;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

public class DAOtests {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static ClearService clearService;
    private static GameService gameService;
    private static LoginService loginService;
    private static LogoutService logoutService;
    private static RegistrationService registrationService;

    @BeforeEach
    public void beforeEach() throws DataAccessException {
        DatabaseManager.createDatabase();

        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(gameDAO, authDAO, userDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        registrationService = new RegistrationService(userDAO, authDAO);

        clearService.clearAll();
    }

    @Test
    public void testGetAuth() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";
        final var authData = new AuthData(authToken, username);

        authDAO.createAuth(username);
        final var result = authDAO.getAuth(authToken);
        Assertions.assertEquals(result, authData);
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

        authDAO.createAuth(username);
        final var result = authDAO.getAuth(authToken);

        Assertions.assertEquals(result, authData);
    }

    @Test
    public void testCreateAuthInvalid() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(username);
        });

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null);
        });
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        final var username = "username";
        final var authToken = "authtoken";

        authDAO.createAuth(username);
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

        authDAO.createAuth(username);
        authDAO.createAuth(username2);
        authDAO.deleteAllAuth();

        final var result = authDAO.getAuth(authToken);
        final var result2 = authDAO.getAuth(authToken2);

        Assertions.assertNull(result);
        Assertions.assertNull(result2);
    }
}
