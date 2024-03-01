package server.handlers;

import java.util.HashMap;
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

    public Object handleListGames(Request request, Response response) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            String authToken = request.headers("Authorization");

            if (authToken == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            UserData user = gameService.getUserByAuth(authToken);

            if (user == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            GameData[] games = gameService.listGames(user.getUsername());

            response.status(200);
            responseData.put("games", games);
            return new Gson().toJson(responseData);
        } catch (Exception e) {
            response.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }
}