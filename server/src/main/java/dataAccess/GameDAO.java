package dataAccess;

import model.GameData;

import java.util.HashMap;

public class GameDAO {
    private static HashMap<Integer, GameData> gamesList = new HashMap<Integer, GameData>();

    public void deleteAllGames(){
        gamesList.clear();
    }
}
