package dataAccessTests;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.mysql.cj.log.Log;
import dataAccess.*;
import model.AuthData;
import model.GameData;
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

    //GameDAO tests

    @Test
    public void getGame() throws DataAccessException {
        final var gameId = 1;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        gameDAO.createGame(gameData);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, gameData);
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        final var gameId = 1;
        final var result = gameDAO.getGame(gameId);
        Assertions.assertNull(result);
    }

//    @Test
//    public void testListGames() throws DataAccessException {
//        final var game1Id = 1;
//        final var game2Id = 2;
//        final var whiteUsername1 = "username1";
//        final var whiteUsername2 = "username2";
//        final var blackUsername1 = "username3";
//        final var blackUsername2 = "username4";
//        final var gameName1 = "gameName1";
//        final var gameName2 = "gameName2";
//        final var game1 = new ChessGame();
//        final var game2 = new ChessGame();
//        final var gameData1 = new GameData(game1Id, whiteUsername1, blackUsername1, gameName1, game1);
//        final var gameData2 = new GameData(game2Id, whiteUsername2, blackUsername2, gameName2, game2);
//        gameDAO.createGame(gameData1);
//        gameDAO.createGame(gameData2);
//        final var result = gameDAO.listGames(whiteUsername1);
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(result.size(), 2);
//        Assertions.assertTrue(result.contains(gameData1));
//        Assertions.assertTrue(result.contains(gameData2));
//    }
//
//    @Test
//    public void testListGamesNotFound() throws DataAccessException {
//        final var username = "username";
//        final var result = gameDAO.listGames(username);
//        Assertions.assertNotNull(result);
//        Assertions.assertTrue(result instanceof Collection);
//        Assertions.assertEquals(result.size(), 0);
//    }

    @Test
    public void testCreateGame() throws DataAccessException {
        final var gameId = 1;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        gameDAO.createGame(gameData);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, gameData);
    }

    @Test
    public void testCreateInvalidGame() throws DataAccessException {
        final var gameId = 1;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameData1 = new GameData(-1, whiteUsername, blackUsername, gameName, game);
        final var gameData2 = new GameData(gameId, whiteUsername, blackUsername, null, game);
        final var gameData3 = new GameData(gameId, whiteUsername, blackUsername, gameName, null);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData1));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData2));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData3));
    }

    @Test
    public void testUpdateGame() throws DataAccessException, InvalidMoveException {
        final var gameId = 1;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        gameDAO.createGame(gameData);
        final var newGame = new ChessGame();
        newGame.setTeamTurn(ChessGame.TeamColor.BLACK);
        final var newGameData = new GameData(gameId, whiteUsername, blackUsername, gameName, newGame);
        gameDAO.updateGame(newGameData);
        final var result = gameDAO.getGame(gameId);
        Assertions.assertEquals(result, newGameData);
    }

    @Test
    public void testInvalidUpdateGame() throws DataAccessException {
        final var gameId = 1;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var gameName = "gameName";
        final var game = new ChessGame();
        final var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        gameDAO.createGame(gameData);
        final var newGame = new ChessGame();
        final var newGameData = new GameData(-1, whiteUsername, blackUsername, gameName, newGame);
        final var newGameData2 = new GameData(gameId, whiteUsername, blackUsername, null, newGame);
        final var newGameData3 = new GameData(gameId, whiteUsername, blackUsername, gameName, null);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameData));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameData2));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameData3));
    }

//    @Test
//    public void testDeleteGame() throws DataAccessException {
//        final var gameId = 1;
//        final var whiteUsername = "username";
//        final var blackUsername = "username2";
//        final var gameName = "gameName";
//        final var game = new ChessGame();
//        final var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
//        gameDAO.createGame(gameData);
//        gameDAO.deleteGame(gameId);
//        final var result = gameDAO.getGame(gameId);
//        Assertions.assertEquals(result, null);
//    }
//
//    @Test
//    public void testDeleteNonExistentGame() throws DataAccessException {
//        final var gameId = 1;
//        gameDAO.deleteGame(gameId);
//        gameDAO.deleteGame(gameId);
//        final var result = gameDAO.getGame(gameId);
//        Assertions.assertEquals(result, null);
//    }

    @Test
    public void testDeleteAllGames() throws DataAccessException {
        final var gameId1 = 1;
        final var gameId2 = 2;
        final var whiteUsername = "username";
        final var blackUsername = "username2";
        final var whiteUsername2 = "username3";
        final var blackUsername2 = "username4";
        final var gameName = "gameName";
        final var gameName2 = "gameName2";
        final var game = new ChessGame();
        final var game2 = new ChessGame();
        final var gameData1 = new GameData(gameId1, whiteUsername, blackUsername, gameName, game);
        final var gameData2 = new GameData(gameId2, whiteUsername2, blackUsername2, gameName2, game2);
        gameDAO.createGame(gameData1);
        gameDAO.createGame(gameData2);
        gameDAO.deleteAllGames();
        final var result1 = gameDAO.getGame(gameId1);
        final var result2 = gameDAO.getGame(gameId2);
        Assertions.assertNull(result1);
        Assertions.assertNull(result2);
    }

//    @Test
//    public void getMaxGameId() throws DataAccessException {
//        final var gameId1 = 10;
//        final var gameId2 = 200;
//        final var whiteUsername = "username";
//        final var blackUsername = "username2";
//        final var whiteUsername2 = "username3";
//        final var blackUsername2 = "username4";
//        final var gameName = "gameName";
//        final var gameName2 = "gameName2";
//        final var game = new ChessGame();
//        final var game2 = new ChessGame();
//        final var gameData1 = new GameData(gameId1, whiteUsername, blackUsername, gameName, game);
//        final var gameData2 = new GameData(gameId2, whiteUsername2, blackUsername2, gameName2, game2);
//        gameDAO.createGame(gameData1);
//        gameDAO.createGame(gameData2);
//        final var result = gameDAO.getMaxGameId();
//        Assertions.assertEquals(result, gameId2);
//    }

}
