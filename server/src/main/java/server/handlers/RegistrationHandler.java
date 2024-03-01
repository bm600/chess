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

    private static record RequestBody(String username, String password, String email) {
    }

    public Object handleRegistration(Request req, Response res) {
        String username;
        String password;
        String email;

        try {
            final RequestBody requestBody = new Gson().fromJson(req.body(), RequestBody.class);
            username = requestBody.username();
            password = requestBody.password();
            email = requestBody.email();
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
            // Look up the user to see if they already exist
            final UserData existingUser = registrationService.getUser(username);

            if (existingUser != null) {
                res.status(403);
                return new Gson().toJson(Map.of(
                        "message", "Error: already taken"
                ));
            }

            // If the user does not exist, create them
            registrationService.createUser(new UserData(username, password, email));

            // Create a new session for the user
            final AuthData auth = registrationService.createAuth(username);

            // Respond with the auth token and username
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
