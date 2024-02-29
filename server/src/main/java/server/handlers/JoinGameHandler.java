package server.handlers;

import java.util.Map;

import com.google.gson.Gson;

import chess.ChessGame;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private GameService gameService;

    public static record RequestBody(String playerColor, int gameID) {
    }

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) {
        String playerColorString;
        int gameId;

        try {
            final var requestBody = new Gson().fromJson(req.body(), RequestBody.class);
            playerColorString = requestBody.playerColor();
            gameId = requestBody.gameID();
            if (gameId < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }

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

            final var playerColor = playerColorString == null ? null : ChessGame.TeamColor.valueOf(playerColorString);

            final var game = gameService.getGame(gameId);
            if (game == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }

            if (playerColor != null) {
                final var requestedColorUsername = playerColor == ChessGame.TeamColor.WHITE ? game.getWhiteUsername()
                        : game.getBlackUsername();
                if (requestedColorUsername != null) {
                    res.status(403);
                    return new Gson().toJson(Map.of("message", "Error: already taken"));
                }
                gameService.addPlayer(user.getUsername(),gameId,  playerColor);
            }

            res.status(200);
            return "";
        } catch (Exception e) {
            res.status(500);
            return "Error: Internal server error";
        }
    }
}