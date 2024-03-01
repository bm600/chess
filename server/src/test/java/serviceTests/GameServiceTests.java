package serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.GameData;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    private static UserDAO userDAO;

    private static GameService gameService;

    @BeforeEach
    void beforeEach() {
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();
        UserDAO userDAO = new UserDAO();
        gameService = new GameService(gameDAO, authDAO, userDAO);
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
}