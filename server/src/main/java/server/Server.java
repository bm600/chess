package server;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import server.handlers.*;
import service.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //TODO change so SQLDAOs are implemented instead of MemDAO
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameService gameService = new GameService(gameDAO, authDAO, userDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        RegistrationService registrationService = new RegistrationService(userDAO, authDAO);


        // Clear
        Spark.delete("/db", (request, response) -> new ClearHandler(clearService).handleClear(response));

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
