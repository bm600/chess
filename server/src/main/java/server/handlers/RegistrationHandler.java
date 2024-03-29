package server.handlers;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.RegistrationService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.Map;

public class RegistrationHandler {
    private final RegistrationService registrationService;

    public RegistrationHandler(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    private record RequestBody(String username, String password, String email) {
    }

    public Object handleRegistration(Request req, Response res){
    String username;
    String password;
    String email;

        try {
        final var requestBody = new Gson().fromJson(req.body(), RequestBody.class);
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
        // look up the user to see if they exist already
        final var existingUser = registrationService.getUser(username);

        if (existingUser != null) {
            res.status(403);
            return new Gson().toJson(Map.of(
                    "message", "Error: already taken"
            ));
        }

        var encryptedPassword = registrationService.encryptPassword(password);

        // if the user does not exist, create them
        registrationService.createUser(new UserData(username, encryptedPassword, email));

        // create a new session for the user
        final var auth = registrationService.createAuth(username);

        // respond with the auth token and username
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
