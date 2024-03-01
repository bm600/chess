package server.handlers;

import model.AuthData;
import model.UserData;
import service.LoginService;
import spark.Request;
import spark.Response;
import java.util.Map;
import com.google.gson.Gson;
public class LoginHandler {

    private LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }

    private static record RequestBody(String username, String password) {
    }
    public Object handleLogin(Request req, Response res) {
        String username;
        String password;

        try {
            final RequestBody requestBody = new Gson().fromJson(req.body(), RequestBody.class);
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
            final UserData user = loginService.getUser(username);

            if (user == null || !user.getPassword().equals(password)) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: unauthorized"));
            }

            final AuthData newAuth = loginService.createAuth(username);

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
}
