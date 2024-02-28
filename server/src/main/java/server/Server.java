package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void deleteData(Request req, Response res) {

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
