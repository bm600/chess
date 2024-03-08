package dataAccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private static HashMap<Integer, GameData> gamesList = new HashMap<Integer, GameData>();

    public void deleteAllGames(){
        gamesList.clear();
    }

    public GameData getGame(int gameID) {
        return gamesList.get(gameID);
    }

    public void updateGame(GameData newGame) throws DataAccessException {
        final var currGame = gamesList.get(newGame.getGameID());
        if (currGame != null){
            gamesList.put(newGame.getGameID(), newGame);
        }
        else{
            throw new DataAccessException("Game " + newGame.getGameID() + " is not a valid game.");
        }
    }

    public GameData createGame(GameData game) {
        gamesList.put(game.getGameID(), game);
        return game;
    }

    public GameData[] listGames(String username) {
        return gamesList.values().toArray(new GameData[0]);
    }

    public int getNextGameId() {
        return gamesList.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }

}
