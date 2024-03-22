package clientTests;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import ui.*;
import ui.ClientException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static ServerFacade serverFacade;
    UserData newUser = new UserData("alphaUser", "pass123", "alpha@email.com");
    private static Server testServer;

    @BeforeAll
    public static void startTestServer() {
        testServer = new Server();
        int port = testServer.run(0);
        serverFacade = new ServerFacade("localhost", port);
        System.out.println("Test server running on port: " + port);
    }

    @AfterAll
    public static void stopTestServer() {
        testServer.stop();
    }

    @BeforeEach
    public void resetDatabase() {
        try {
            serverFacade.clear();
        } catch (ClientException e) {
            System.out.println("Database reset failed: " + e.getMessage());
        }
    }

    @Test
    public void verifyDatabaseCleared() {
        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    public void registerUserAndVerifyToken() {
        var userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        assertNotNull(userToken);
        assertEquals(newUser.getUsername(), userToken.getUsername());
        assertNotNull(userToken.getAuthToken());
    }

    @Test
    public void joinNonExistingGameFails() {
        AuthData userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        int gameId = 7;
        ClientException exception = assertThrows(ClientException.class,
                () -> serverFacade.join(userToken, gameId, "WHITE"));
        assertEquals("Failed to join game: Error: bad request", exception.getMessage());
    }

    @Test
    public void duplicateUserRegistrationFails() {
        var firstToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        assertNotNull(firstToken);

        var duplicateError = assertThrows(ClientException.class, () -> serverFacade.register(newUser));
        assertEquals("Error: already taken", duplicateError.getMessage());
    }

    @Test
    public void loginUserWithSuccess() {
        var registrationToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        assertNotNull(registrationToken);

        var loginToken = assertDoesNotThrow(() -> serverFacade.login(newUser));
        assertNotNull(loginToken);
        assertEquals(newUser.getUsername(), loginToken.getUsername());
        assertNotNull(loginToken.getAuthToken());

        assertNotEquals(registrationToken.getAuthToken(), loginToken.getAuthToken());
    }

    @Test
    public void loginFailureReturnsUnauthorized() {
        var loginError = assertThrows(ClientException.class, () -> serverFacade.login(newUser));
        assertEquals("Error: Unauthorized", loginError.getMessage());
    }

    @Test
    public void successfulLogout() {
        var authToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        assertDoesNotThrow(() -> serverFacade.logout(authToken));

        var error = assertThrows(ClientException.class, () -> serverFacade.listGames(authToken));
        assertEquals("Failed to list games: Error: Unauthorized", error.getMessage());
    }

    @Test
    public void logoutWithoutAuthFails() {
        var invalidToken = new AuthData("unknownUser");
        var error = assertThrows(ClientException.class, () -> serverFacade.logout(invalidToken));
        assertEquals("Error: Unauthorized", error.getMessage());
    }

    @Test
    public void listGamesAfterCreation() {
        var authToken = assertDoesNotThrow(() -> serverFacade.register(newUser));

        var createdGame = assertDoesNotThrow(() -> serverFacade.createGame(authToken, "Alpha Game"));
        assertNotNull(createdGame);
        var gameId = createdGame.getGameID();

        var gameList = assertDoesNotThrow(() -> serverFacade.listGames(authToken));
        assertNotNull(gameList);
        assertTrue(gameList.stream().anyMatch(game -> game.getGameID() == gameId));
    }

    @Test
    public void listGamesUnauthorized() {
        var fakeToken = new AuthData("fakeUser");
        var error = assertThrows(ClientException.class, () -> serverFacade.listGames(fakeToken));
        assertEquals("Failed to list games: Error: Unauthorized", error.getMessage());
    }

    @Test
    public void createGameWithSuccess() {
        var userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        var newGame = assertDoesNotThrow(() -> serverFacade.createGame(userToken, "New Alpha Game"));
        assertNotNull(newGame);
    }

    @Test
    public void unauthorizedGameCreationFails() {
        var unauthorizedToken = new AuthData("unauthorizedUser");
        var error = assertThrows(ClientException.class, () -> serverFacade.createGame(unauthorizedToken, "Unauthorized Alpha Game"));
        assertEquals("Error: Unauthorized", error.getMessage());
    }

    @Test
    public void joinGameWithSuccess() {
        var userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        var gameToJoin = assertDoesNotThrow(() -> serverFacade.createGame(userToken, "Alpha Join Game"));
        assertDoesNotThrow(() -> serverFacade.join(userToken, gameToJoin.getGameID(), "WHITE"));
    }

    @Test
    public void joinOccupiedSlotFails() {
        var authToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        var game = assertDoesNotThrow(() -> serverFacade.createGame(authToken, "Occupied Game"));
        assertDoesNotThrow(() -> serverFacade.join(authToken, game.getGameID(), "WHITE"));
        var ex = assertThrows(ClientException.class, () -> serverFacade.join(authToken, game.getGameID(), "WHITE"));
        assertEquals("Failed to join game: Error: Already taken", ex.getMessage());
    }

    @Test
    public void observeGameReturnsTrue() {
        var userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        var gameToObserve = assertDoesNotThrow(() -> serverFacade.createGame(userToken, "Alpha Observe Game"));
        var doesExist = assertDoesNotThrow(() -> serverFacade.observe(userToken, gameToObserve.getGameID()));
        assertTrue(doesExist);
    }

    @Test
    public void observeGameReturnsFalse() {
        var userToken = assertDoesNotThrow(() -> serverFacade.register(newUser));
        var doesExist = assertDoesNotThrow(() -> serverFacade.observe(userToken, 500));
        assertFalse(doesExist);
    }
}