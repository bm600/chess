package server.handlers;

import model.AuthData;
import service.LogoutService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LogoutHandler {
    private LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService){
        this.logoutService = logoutService;
    }

    public Object handleLogout(Request request, Response response) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            String authToken = extractAuthToken(request);

            if (authToken == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            AuthData authData = logoutService.getAuth(authToken);

            if (authData == null) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            logoutService.deleteAuth(authToken);
            response.status(200);
            return "";
        } catch (Exception e) {
            response.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }

    private String extractAuthToken(Request request) {
        return request.headers("Authorization");
    }
}
