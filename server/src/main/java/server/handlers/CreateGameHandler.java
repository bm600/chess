package server.handlers;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private GameService gameService;

    public static record RequestBody(String gameName) {}

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleCreateGame(Request req, Response res) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            String gameName;

            final RequestBody requestBody = new Gson().fromJson(req.body(), RequestBody.class);
            gameName = requestBody.gameName();
            if (gameName == null) {
                throw new Exception();
            }

            final String authToken = req.headers("Authorization");
            if (authToken == null) {
                res.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            final UserData user = gameService.getUserByAuth(authToken);
            if (user == null) {
                res.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            final int gameId = gameService.getNextGameID();
            final GameData gameData = gameService.createGame(new GameData(
                    gameId,
                    null,
                    null,
                    gameName,
                    new ChessGame()));

            gameService.createGame(gameData);

            res.status(200);
            responseData.put("gameID", gameId);
            return new Gson().toJson(responseData);
        } catch (Exception e) {
            res.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }
}