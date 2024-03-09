package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO extends SQLDAO implements GameDAO {

    private static final String TABLE = "game";

    static private final String[] createStatements = {
            String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        `gameId` varchar(256),
                        `wUsername` varchar(256),
                        `bUsername` varchar(256),
                        `gameName` varchar(256),
                        `game` varchar(256),
                        PRIMARY KEY (`gameId`),
                        INDEX(gameName)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """, TABLE)
    };

    static {
        try {
            configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = String.format("TRUNCATE %s", TABLE);
        executeUpdate(statement);
    }

    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("SELECT * FROM %s WHERE gameId=?", TABLE);
            try (var gm = conn.prepareStatement(statement)) {
                gm.setInt(1, gameID);
                try (var rs = gm.executeQuery()) {
                    if (rs.next()) {
                        var wUsername = rs.getString("wUsername");
                        var bUsername = rs.getString("bUsername");
                        var gameName = rs.getString("gameName");
                        var gameJSON = rs.getString("game");
                        var game = new Gson().fromJson(gameJSON, ChessGame.class);
                        return new GameData(gameID,wUsername, bUsername, gameName, game);
                    }
                }
            }
            return null;
        }
        catch (DataAccessException | SQLException e) {
            throw new RuntimeException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        if(isValidGame(newGame)){
            throw new DataAccessException("Invalid game data");
        }
        var statement = String.format("UPDATE %s SET wUsername=?, bUsername=?, gameName=?, game=? WHERE gameId=?", TABLE);
        var game = new Gson().toJson(newGame.getGame());
        executeUpdate(statement, newGame.getWhiteUsername(), newGame.getBlackUsername(), newGame.getGameName(), game, newGame.getGameID());
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        if (isValidGame(game)){
            throw new DataAccessException("Invalid game data");
        }
        var statement = String.format("INSERT INTO %s (gameId, wUsername, bUsername, gameName, game) VALUES (?, ?, ?, ?, ?)", TABLE);
        var newGame = new Gson().toJson(game.getGame());
        executeUpdate(statement, game.getGameID(), game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), newGame);
        return game;
    }

    @Override
    public GameData[] listGames(String username) throws DataAccessException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var gameId = rs.getInt("gameId");
                        var wUsername = rs.getString("wUsername");
                        var bUsername = rs.getString("bUsername");
                        var gameName = rs.getString("gameName");
                        var gameJSON = rs.getString("game");
                        var game = new Gson().fromJson(gameJSON, ChessGame.class);
                        result.add(new GameData(gameId,wUsername, bUsername, gameName, game));

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result.toArray(new GameData[0]);
    }

    @Override
    public int getNextGameId() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = String.format("SELECT MAX(gameId) FROM %s", TABLE);
            try(var ps = conn.prepareStatement(statement)){
                try(var rs = ps.executeQuery()){
                    if (rs.next()) {
                        return rs.getInt(1) + 1;
                    }
                }
            }
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
        throw new UnsupportedOperationException("Error");
    }

    private boolean isValidGame(GameData game){
        return !(game.getGameID() >= 0 & game.getGameName() != null & game.getGame() != null);
    }
}
