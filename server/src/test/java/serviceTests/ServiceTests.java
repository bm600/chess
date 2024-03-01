package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
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
        assertNull(authResult);
    }

    @Test
    public void shouldClearApplication() throws DataAccessException {
        final String username = "username";
        final String password = "password";
        final String email = "email";
        final UserData user1 = new UserData(username, password, email);

        final int gameID = 1;
        final String gameName = "gameName";
        final GameData game1 = new GameData(gameID, null, null, gameName, new ChessGame());

        registrationService.createUser(user1);
        final AuthData auth1 = registrationService.createAuth(username);
        gameService.createGame(game1);

        clearService.clearAll();

        final var userResult = loginService.getUser(username);
        final var authResult = loginService.getAuth(auth1.getAuthToken());
        final var gameResult = gameService.getGame(gameID);

        assertNull(userResult);
        assertNull(authResult);
        assertNull(gameResult);
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
        assertNull(userResult);
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
    void testListGames() {
        final String username = "username";
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());
        final GameData game2 = new GameData(2, null, null, "gameName2", new ChessGame());

        gameService.createGame(game1);
        gameService.createGame(game2);

        final var games = gameService.listGames(username);

        assertEquals(2, games.length);
    }

    @Test
    void testEmptyGames() {
        final String username = "username";

        final var games = gameService.listGames(username);

        assertEquals(0, games.length);
    }

    @Test
    void testDeleteGames() {
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());

        gameService.createGame(game1);
        gameService.deleteAllGames();

        final var games = gameService.listGames(null);

        assertEquals(0, games.length);
    }

    @Test
    void testDeleteAllGamesAlreadyEmpty() {
        gameService.deleteAllGames();

        final var games = gameService.listGames(null);

        assertEquals(0, games.length);
    }

    @Test
    void testCreateGame() {
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());

        gameService.createGame(game1);
        final var gameResult = gameService.getGame(1);

        assertEquals(1, gameResult.getGameID());
        assertEquals("gameName", gameResult.getGameName());
    }

    @Test
    void createGameIllegalArguments() {
        final GameData game1 = new GameData(-1, null, null, null, new ChessGame());

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createGame(game1);
        });
    }

    @Test
    void testGetGame() {
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());

        gameService.createGame(game1);
        final var gameResult = gameService.getGame(1);

        assertEquals(1, gameResult.getGameID());
        assertEquals("gameName", gameResult.getGameName());
    }

    @Test
    void testReturnNull() {
        final var gameResult = gameService.getGame(1);

        assertNull(gameResult);
    }

    @Test
    void testGetNextGameId() {
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());

        gameService.createGame(game1);
        final var nextGameId = gameService.getNextGameID();

        assertEquals(2, nextGameId);
    }



    @Test
    void testGetGameID1() {
        final var nextGameId = gameService.getNextGameID();

        assertEquals(1, nextGameId);
    }

    @Test
    void testAddPlayer() throws DataAccessException {
        final GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());

        gameService.createGame(game1);
        gameService.addPlayer("username", 1, ChessGame.TeamColor.WHITE);

        final var gameResult = gameService.getGame(1);

        assertEquals("username", gameResult.getWhiteUsername());
    }

    @Test
    void testThrowDataAccessExceptionAddPlayerToNonexistentGame() {
        assertThrows(DataAccessException.class, () -> {
            gameService.addPlayer("username", 1, ChessGame.TeamColor.WHITE);
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

        assertNull(authResult);
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
        assertNull(authResult);
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

        assertNull(userResult);
    }

    @Test
    public void shouldNotThrowWhenNoUsers() {
        clearService.deleteAllUsers();
    }

}