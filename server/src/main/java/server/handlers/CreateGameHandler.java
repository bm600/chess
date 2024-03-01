package server.handlers;

import java.util.Map;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameData;
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
        String gameName;

        try {
            final RequestBody requestBody = new Gson().fromJson(req.body(), RequestBody.class);
            gameName = requestBody.gameName();
            if (gameName == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }

        try {
            final String authToken = req.headers("Authorization");
            if (authToken == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            final var user = gameService.getUserByAuth(authToken);
            if (user == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            final int gameId = gameService.getNextGameID();
            final var gameData = gameService.createGame(new GameData(
                    gameId,
                    null,
                    null,
                    gameName,
                    new ChessGame()));

            gameService.createGame(gameData);

            res.status(200);
            return new Gson().toJson(Map.of(
                    "gameID", gameId
            ));
        } catch (Exception e) {
            res.status(500);
            return "Error: Internal server error";
        }
    }
}