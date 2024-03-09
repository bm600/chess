package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;


import java.sql.SQLException;

import static dataAccess.DatabaseManager.createDatabase;
import static org.junit.jupiter.api.Assertions.fail;

public class SQLDAOTests {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;

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
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        clearService.clearAll();
    }

    @Test
    public void testUserAuthentication() throws DataAccessException {
        final var user = "username";
        final var token = "authtoken";
        final var authInfo = new AuthData(token, user);

        authDAO.createAuth(user, token);
        final var result = authDAO.getAuth(token);

        Assertions.assertEquals(result, authInfo);
    }

    @Test
    public void testUserAuthenticationNotFound() throws DataAccessException {
        final var token = "authtoken";
        final var result = authDAO.getAuth(token);
        Assertions.assertNull(result);
    }

    @Test
    public void testUserAuthCreation() throws DataAccessException {
        final var user = "username";
        final var token = "authtoken";
        final var authInfo = new AuthData(token, user);

        authDAO.createAuth(user, token);
        final var result = authDAO.getAuth(token);

        Assertions.assertEquals(result, authInfo);
    }

    @Test
    public void testUserAuthCreationInvalid() {
        final var user = "username";
        final var token = "authtoken";

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null, null);
        });
    }

    @Test
    public void testUserAuthDeletion() throws DataAccessException {
        final var user = "username";
        final var token = "authtoken";

        authDAO.createAuth(user, token);
        authDAO.deleteAuth(token);

        final var result = authDAO.getAuth(token);
        Assertions.assertNull(result);
    }

    @Test
    public void testUserAuthDeletionNotFound() throws DataAccessException {
        final var token = "authtoken";

        authDAO.deleteAuth(token);

        final var result = authDAO.getAuth(token);
        Assertions.assertNull(result);
    }

    @Test
    public void testUserAuthDeletionAll() throws DataAccessException {
        final var user = "username";
        final var user2 = "username2";
        final var token = "authtoken";
        final var token2 = "authtoken2";

        authDAO.createAuth(user, token);
        authDAO.createAuth(user2, token2);
        authDAO.deleteAllAuth();

        final var result = authDAO.getAuth(token);
        final var result2 = authDAO.getAuth(token2);

        Assertions.assertNull(result);
        Assertions.assertNull(result2);
    }

    @Test
    public void testUserCreation() throws DataAccessException {
        var user = "username";
        var pass = "password1";
        var mail = "email@email.com";
        var newUser = new UserData(user, pass, mail);
        var insertedUser = userDAO.createUser(newUser);

        Assertions.assertEquals(newUser, insertedUser);
    }

    @Test
    public void testUserCreationInvalid() {
        final var user = "username";
        final var mail = "email";
        final var pass = "password";

        final var user1 = new UserData(null, mail, pass);
        final var user2 = new UserData(user, null, pass);
        final var user3 = new UserData(user, mail, null);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user1));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user3));
    }

    @Test
    public void testUserRetrieval() throws DataAccessException {
        final var user = "username90";
        final var mail = "email90";
        final var pass = "password90";

        final var userInfo = new UserData(user, pass, mail);
        userDAO.createUser(userInfo);
        final var result = userDAO.getUser(user);
        Assertions.assertEquals(result, userInfo);
    }

    @Test
    public void testUserDeletionAll() throws DataAccessException {
        final var user = "username";
        final var user2 = "username2";
        final var pass = "password";
        final var pass2 = "password2";
        final var mail = "email1@email.com";
        final var mail2 = "email2@email.com";

        userDAO.createUser(new UserData(user, pass, mail));
        userDAO.createUser(new UserData(user2, pass2, mail2));
        userDAO.deleteAllUsers();

        final var result = userDAO.getUser(user);
        final var result2 = userDAO.getUser(user2);

        Assertions.assertNull(result);
        Assertions.assertNull(result2);
    }

//GameDAO tests

    @Test
    public void testGameRetrieval() throws DataAccessException {
        final var gameId = 1;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameInfo = new GameData(gameId, whiteUser, blackUser, gameName, game);
        gameDAO.createGame(gameInfo);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, gameInfo);
    }

    @Test
    public void testGameNotFound() throws DataAccessException {
        final var gameId = 1;
        final var result = gameDAO.getGame(gameId);
        Assertions.assertNull(result);
    }

    @Test
    public void testListGamesEmpty() {
        try {
            GameData[] games = gameDAO.listGames("username");

            Assertions.assertNotNull(games);
            Assertions.assertTrue(games.length == 0);

        } catch (DataAccessException e) {
            fail("Exception should not be thrown for positive test");
        }
    }

    @Test
    public void testListGamesNotEmpty() {
        try {
            var game = new ChessGame();
            gameDAO.createGame(new GameData(1, "user23", "buser34", "epicGame", game));
            GameData[] games = gameDAO.listGames("username");

            Assertions.assertNotNull(games);
            Assertions.assertTrue(games.length > 0);

        } catch (DataAccessException e) {
            fail("Exception should not be thrown for positive test");
        }
    }

    @Test
    public void testGameCreation() throws DataAccessException {
        final var gameId = 1;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameInfo = new GameData(gameId, whiteUser, blackUser, gameName, game);
        gameDAO.createGame(gameInfo);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, gameInfo);
    }

    @Test
    public void testGameCreationInvalid() {
        final var gameId = 1;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameInfo1 = new GameData(-1, whiteUser, blackUser, gameName, game);
        final var gameInfo2 = new GameData(gameId, whiteUser, blackUser, null, game);
        final var gameInfo3 = new GameData(gameId, whiteUser, blackUser, gameName, null);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameInfo1));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameInfo2));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameInfo3));
    }

    @Test
    public void testGameUpdate() throws DataAccessException {
        final var gameId = 1;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameInfo = new GameData(gameId, whiteUser, blackUser, gameName, game);
        gameDAO.createGame(gameInfo);
        final var newGame = new ChessGame();
        newGame.setTeamTurn(ChessGame.TeamColor.BLACK);
        final var newGameInfo = new GameData(gameId, whiteUser, blackUser, gameName, newGame);
        gameDAO.updateGame(newGameInfo);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, newGameInfo);
    }

    @Test
    public void testInvalidGameUpdate() throws DataAccessException {
        final var gameId = 1;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameInfo = new GameData(gameId, whiteUser, blackUser, gameName, game);
        gameDAO.createGame(gameInfo);
        final var newGame = new ChessGame();
        final var newGameInfo = new GameData(-1, whiteUser, blackUser, gameName, newGame);
        final var newGameInfo2 = new GameData(gameId, whiteUser, blackUser, null, newGame);
        final var newGameInfo3 = new GameData(gameId, whiteUser, blackUser, gameName, null);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameInfo));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameInfo2));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameInfo3));
    }

    @Test
    public void testDeleteAllGames() throws DataAccessException {
        final var gameId1 = 1;
        final var gameId2 = 2;
        final var whiteUser = "username";
        final var blackUser = "username2";
        final var whiteUser2 = "username3";
        final var blackUser2 = "username4";
        final var gameName = "gameName";
        final var gameName2 = "gameName2";
        final var game = new ChessGame();
        final var game2 = new ChessGame();
        final var gameInfo1 = new GameData(gameId1, whiteUser, blackUser, gameName, game);
        final var gameInfo2 = new GameData(gameId2, whiteUser2, blackUser2, gameName2, game2);
        gameDAO.createGame(gameInfo1);
        gameDAO.createGame(gameInfo2);
        gameDAO.deleteAllGames();
        final var result1 = gameDAO.getGame(gameId1);
        final var result2 = gameDAO.getGame(gameId2);
        Assertions.assertNull(result1);
        Assertions.assertNull(result2);
    }

    @Test
    public void testGetNextGameIdPositive() {
        try {
            int nextGameId = gameDAO.getNextGameId();
            Assertions.assertTrue(nextGameId > 0);
        } catch (SQLException | DataAccessException e) {
            fail("Exception should not be thrown for positive test");
        }
    }

}
