package service;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;

import java.sql.SQLException;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    private final UserDAO userDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUserByAuth(String authToken) throws DataAccessException {
        final var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return null;
        }
        return userDAO.getUser(auth.getUsername());
    }

    public GameData getGame(int gameID) throws DataAccessException {
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

    public int getNextGameID() throws SQLException, DataAccessException {
        return gameDAO.getNextGameId();
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        if (gameData.getGameID() < 0 || gameData.getGameName() == null)
            throw new IllegalArgumentException("Invalid game data");
        return gameDAO.createGame(gameData);
    }

    public GameData[] listGames(String username) throws DataAccessException {
        return gameDAO.listGames(username);
    }

    public void deleteAllGames() throws DataAccessException {
        gameDAO.deleteAllGames();
    }
}
