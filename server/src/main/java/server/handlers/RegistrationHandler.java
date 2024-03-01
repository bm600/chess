package server.handlers;

import model.AuthData;
import model.UserData;
import service.RegistrationService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.Map;

public class RegistrationHandler {
    private RegistrationService registrationService;

    public RegistrationHandler(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    private record RequestBody(String username, String password, String email) {
    }

    public Object handleRegistration(Request req, Response res) {
        String email;
        String password;
        String username;

        try {
            final RequestBody requestBody = new Gson().fromJson(req.body(), RequestBody.class);
            password = requestBody.password(); username = requestBody.username(); email = requestBody.email();
            if (username == null || password == null || email == null) {
                throw new Exception();
            }
        } catch(Exception e) {
            res.status(400);
            return new Gson().toJson(Map.of(
                    "message", "Error: bad request"
            ));
        }

        try {
            final UserData existingUser = registrationService.getUser(username);
            if (existingUser != null) {
                res.status(403);
                return new Gson().toJson(Map.of(
                        "message", "Error: already taken"
                ));
            }
            var newUser = new UserData(username, password, email);
            registrationService.createUser(newUser);
            final AuthData auth = registrationService.createAuth(username);

            return new Gson().toJson(Map.of(
                    "username", username,
                    "authToken", auth.getAuthToken()
            ));
        } catch(Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of(
                    "message", "Error: Internal server error"
            ));
        }
    }


}
