package server.handlers;

import java.util.Map;

import com.google.gson.Gson;

import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");

            if (authToken == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            UserData user = gameService.getUserByAuth(authToken);

            if (user == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            GameData[] games = gameService.listGames(user.getUsername());

            res.status(200);
            return new Gson().toJson(Map.of(
                    "games", games
            ));
        } catch(Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: Internal server error"));
        }
    }
}