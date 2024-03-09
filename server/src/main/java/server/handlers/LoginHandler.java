package server.handlers;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.LoginService;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
public class LoginHandler {

    private final LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }

    public Object handleLogin(Request req, Response res) {
        String username;
        String password;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        try {
            final var requestBody = new Gson().fromJson(req.body(), RequestData.class);
            username = requestBody.username();
            password = requestBody.password();

            if (username == null || password == null) {
                throw new Exception();
            }
        } catch(Exception e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }

        try {
            final var user = loginService.getUser(username);

            if (user == null || !encoder.matches(password, user.getPassword())) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            final var newAuth = loginService.createAuth(username);

            res.status(200);
            return new Gson().toJson(Map.of(
                    "username", username,
                    "authToken", newAuth.getAuthToken()
            ));
        } catch(Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: Internal server error"));
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
