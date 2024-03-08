package dataAccess;

import model.GameData;

import java.sql.SQLException;

public interface GameDAO {


    void deleteAllGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

     void updateGame(GameData newGame) throws DataAccessException;
     GameData createGame(GameData game) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;
    int getNextGameId() throws DataAccessException, SQLException;

}
