package service;

import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.GameData;
import model.UserData;

public class GameService {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    private final MemoryUserDAO userDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO, MemoryUserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUserByAuth(String authToken) {
        final var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return null;
        }
        return userDAO.getUser(auth.getUsername());
    }

    public GameData getGame(int gameID) {
        return gameDAO.getGame(gameID);
    }

    public void addPlayer(String username, int gameID, ChessGame.TeamColor teamColor) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        String whiteUsername = game.getWhiteUsername();
        String blackUsername = game.getBlackUsername();

        if (teamColor == ChessGame.TeamColor.WHITE) {
            whiteUsername = username;
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            blackUsername = username;
        }

        GameData newGame = new GameData(
                gameID,
                whiteUsername,
                blackUsername,
                game.getGameName(),
                game.getGame()
        );

        gameDAO.updateGame(newGame);
    }

    public int getNextGameID() {
        return gameDAO.getNextGameId();
    }

    public GameData createGame(GameData gameData) {
        if (gameData.getGameID() < 0 || gameData.getGameName() == null)
            throw new IllegalArgumentException("Invalid game data");
        return gameDAO.createGame(gameData);
    }

    public GameData[] listGames(String username) {
        return gameDAO.listGames();
    }

    public void deleteAllGames() {
        gameDAO.deleteAllGames();
    }
}
