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
    private final GameService gameService;

    public record RequestBody(String gameName) {}

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleCreateGame(Request req, Response res) {
        try {
            final var authToken = req.headers("Authorization");
            if (authToken == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            }

            final var user = gameService.getUserByAuth(authToken);
            if (user == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            }

            String gameName;
            try {
                final var requestBody = new Gson().fromJson(req.body(), RequestBody.class);
                gameName = requestBody.gameName();
                if (gameName == null) {
                    throw new Exception();
                }
            } catch(Exception e) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: Bad Request"));
            }

            final int gameId = gameService.getNextGameID();
            final var gameData = gameService.createGame(new GameData(
                    gameId,
                    null,
                    null,
                    gameName,
                    new ChessGame()));

            res.status(200);
            return new Gson().toJson(Map.of(
                    "gameID", gameData.getGameID()
            ));
        } catch(Exception e) {
            res.status(500);
            return "Error: Internal Server Issue";
        }
    }
}