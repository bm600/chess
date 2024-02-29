package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.UserData;

public class GameService {
    private GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public UserData getUserByAuth(String authToken){
        throw new RuntimeException("NOT IMPLEMENTED");
    }//TODO possibly delete this

    public GameData getGame(int gameID){
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

    public int getNextGameID(){
        return gameDAO.getNextGameId();
    }

    public GameData createGame(GameData game){
        if(game.getGameName() != null & game.getGameID() >= 0){
            return gameDAO.createGame(game);
        }
        else{
            throw new IllegalArgumentException("Invalid game");
        }
    }

    public GameData[] listGames(String username){
        return gameDAO.listGames();
    }

}
