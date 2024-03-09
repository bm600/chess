package server.handlers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.LoginService;
import spark.Request;
import spark.Response;

import java.util.Map;
import com.google.gson.Gson;
public class LoginHandler {

    private final LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }

    public Object handleLogin(Request req, Response res) {
        try {
            final var requestBody = new Gson().fromJson(req.body(), RequestData.class);
            String username = requestBody.username();
            String password = requestBody.password();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (username == null || password == null) {
                throw new Exception();
            }

            final var user = loginService.getUser(username);
            if (user == null || !encoder.matches(password, user.getPassword())) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            }

            final var newAuth = loginService.createAuth(username);

            res.status(200);
            return new Gson().toJson(Map.of(
                    "username", username,
                    "authToken", newAuth.getAuthToken()
            ));
        } catch(Exception e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: Bad Request"));
        }
    }

    private static record RequestData(String username, String password) {
    }
}
