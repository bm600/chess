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
        Spark.delete("/db", new ClearHandler(clearService)::handleClear);

        // Register
        Spark.post("/user", new RegistrationHandler(registrationService)::handleRegistration);

        // Login
        Spark.post("/session", new LoginHandler(loginService)::handleLogin);

        // Logout
        Spark.delete("/session", new LogoutHandler(logoutService)::handleLogout);

        // List Games
        Spark.get("/game", new ListGamesHandler(gameService)::handleListGames);

        // Create Game
        Spark.post("/game", new CreateGameHandler(gameService)::handleCreateGame);

        // Join Game
        Spark.put("/game", new JoinGameHandler(gameService)::handleJoinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
