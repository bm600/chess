package server.handlers;

import model.AuthData;
import model.UserData;
import service.LoginService;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
public class LoginHandler {

    private LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }

    private static record RequestBody(String username, String password) {
    }
    public Object handleLogin(Request request, Response response) {
        Map<String, Object> responseData = new HashMap<>();

        try {
            String username = null;
            String password = null;

            RequestData requestData = parseRequestData(request);
            if (requestData != null) {
                username = requestData.username();
                password = requestData.password();
            }

            if (username == null || password == null) {
                response.status(400);
                responseData.put("message", "Error: Bad request");
                return new Gson().toJson(responseData);
            }

            UserData user = loginService.getUser(username);

            if (user == null || !user.getPassword().equals(password)) {
                response.status(401);
                responseData.put("message", "Error: Unauthorized");
                return new Gson().toJson(responseData);
            }

            AuthData newAuth = loginService.createAuth(username);

            response.status(200);
            responseData.put("username", username);
            responseData.put("authToken", newAuth.getAuthToken());
            return new Gson().toJson(responseData);
        } catch (Exception e) {
            response.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }

    private RequestData parseRequestData(Request request) {
        try {
            return new Gson().fromJson(request.body(), RequestData.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static record RequestData(String username, String password) {
    }
}
