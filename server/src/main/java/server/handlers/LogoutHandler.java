package server.handlers;

import service.LogoutService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import java.util.Map;

public class LogoutHandler {
    private LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService){
        this.logoutService = logoutService;
    }

    public Object handle(Request req, Response res) {
        try {
            final var authToken = req.headers("Authorization");
            if (authToken == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }
            final var auth = logoutService.getAuth(authToken);
            if (auth == null) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }
            logoutService.deleteAuth(authToken);
            res.status(200);
            return "";
        } catch(Exception e) {
            res.status(500);
            return "Error: Internal server error";
        }
    }
}
