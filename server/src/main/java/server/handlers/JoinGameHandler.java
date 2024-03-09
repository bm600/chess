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

public class JoinGameHandler {
    private final GameService gameService;

    public static record RequestBody(String playerColor, int gameID) {
    }

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleJoinGame(Request request, Response response) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            String playerColorString;
            int gameId;

            final RequestBody requestBody = new Gson().fromJson(request.body(), RequestBody.class);
            playerColorString = requestBody.playerColor();
            gameId = requestBody.gameID();
            if (gameId < 0) {
                throw new Exception();
            }

            String authToken = request.headers("Authorization");
            if (authToken == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }
            final UserData user = gameService.getUserByAuth(authToken);
            if (user == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            final ChessGame.TeamColor playerColor = playerColorString == null ? null : ChessGame.TeamColor.valueOf(playerColorString);

            final GameData game = gameService.getGame(gameId);
            if (game == null) {
                response.status(400);
                responseData.put("message", "Error: Bad request");
                return new Gson().toJson(responseData);
            }

            if (playerColor != null) {
                final String requestedColorUsername = playerColor == ChessGame.TeamColor.WHITE ? game.getWhiteUsername()
                        : game.getBlackUsername();
                if (requestedColorUsername != null) {
                    response.status(403);
                    responseData.put("message", "Error: Already taken");
                    return new Gson().toJson(responseData);
                }
                gameService.addPlayer(user.getUsername(), gameId, playerColor);
            }

            response.status(200);
            return "";
        } catch (Exception e) {
            response.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }
}