package server;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import server.handlers.*;
import service.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameService gameService = new GameService(gameDAO, authDAO, userDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        RegistrationService registrationService = new RegistrationService(userDAO, authDAO);


        // Clear
        Spark.delete("/db", new ClearHandler(clearService)::handle);

        // Register
        Spark.post("/user", new RegistrationHandler(registrationService)::handle);

        // Login
        Spark.post("/session", new LoginHandler(loginService)::handle);

        // Logout
        Spark.delete("/session", new LogoutHandler(logoutService)::handle);

        // List Games
        Spark.get("/game", new ListGamesHandler(gameService)::handle);

        // Create Game
        Spark.post("/game", new CreateGameHandler(gameService)::handle);

        // Join Game
        Spark.put("/game", new JoinGameHandler(gameService)::handle);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
