package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameHelper;
import model.UserData;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ServerFacade {
    private final String serverUrl;
    private final int serverPort;

    public ServerFacade(String serverUrl, int serverPort) {
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
    }

    public ServerFacade() {
        this("localhost", 0);
    }

    public void clear() throws ClientException {
        makeRequest("DELETE", "db", null, null, null);
    }

    public AuthData register(UserData user) throws ClientException {
        return makeRequest("POST", "user", user, AuthData.class, null);
    }

    public AuthData login(UserData user) throws ClientException {
        return makeRequest("POST", "session", user, AuthData.class, null);
    }

    public void logout(AuthData authToken) throws ClientException {
        makeRequest("DELETE", "session", null, null, authToken);
    }

    public List<GameData> listGames(AuthData authToken) throws ClientException {
        try {
            var games = makeRequest("GET", "game", null, GamesList.class, authToken);
            if (games == null) {
                throw new ClientException(HttpURLConnection.HTTP_BAD_REQUEST, "No games available.");
            }
            return games.games().stream().map(GameHelper::toGame).collect(Collectors.toList());
        } catch (ClientException e) {
            throw new ClientException(HttpURLConnection.HTTP_BAD_REQUEST, STR."Failed to list games: \{e.getMessage()}");
        }
    }


    public GameData createGame(AuthData authToken, String gameName) throws ClientException {
        return makeRequest("POST", "game", new GameData(gameName), GameData.class, authToken);
    }

    public void join(AuthData authToken, int gameID, String playerColor) throws ClientException {
        try {
            List<GameData> games = listGames(authToken);

            if (games.isEmpty() || games.size() < gameID) {
                throw new ClientException(HttpURLConnection.HTTP_BAD_REQUEST, "Error: bad request");
            }

            GameData targetGame = games.get(gameID - 1);

            JoinGameRequest request = new JoinGameRequest(targetGame.getGameID(), playerColor);

            makeRequest("PUT", "game", request, null, authToken);
        } catch (ClientException e) {
            throw new ClientException(HttpURLConnection.HTTP_BAD_REQUEST, STR."Failed to join game: \{e.getMessage()}");
        }
    }


    public boolean observe(AuthData authToken, int gameID) throws ClientException {
        var games = makeRequest("GET", "game", null, GamesList.class, authToken);
        assert games != null;
        return games.games().stream().anyMatch(game -> game.gameID() == gameID);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthData authToken) throws ClientException {
        try {
            HttpURLConnection connection = setupConn(path, method, authToken);

            if (request != null) {
                sendData(request, connection);
            }

            verifyResponseCode(connection);

            if (responseClass == null) {
                return null;
            } else {
                return readData(connection, responseClass);
            }
        } catch (IOException e) {
            throw new ClientException(HttpURLConnection.HTTP_BAD_REQUEST, STR."Failed to make request: \{e.getMessage()}");
        }
    }
    private HttpURLConnection setupConn(String path, String method, AuthData authToken) throws IOException, ClientException {
        URL url = makeUrl(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setReadTimeout(5000);

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken.getAuthToken());
        }

        return connection;
    }

    private URL makeUrl(String path) throws ClientException {
        try {
            return new URI("http", null, serverUrl, serverPort, "/" + path, null, null).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new ClientException(400, STR."Failed to build URL: \{e.getMessage()}");
        }
    }

    private void sendData(Object data, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = new Gson().toJson(data).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    private void verifyResponseCode(HttpURLConnection connection) throws IOException, ClientException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            ErrorResponse errorResponse = readData(connection, ErrorResponse.class);
            throw new ClientException(connection.getResponseCode(), errorResponse.message());
        }
    }

    private <T> T readData(HttpURLConnection connection, Class<T> classOfT) throws ClientException, IOException {
        InputStream responseBody;

        try {
            responseBody = connection.getInputStream();
        } catch (IOException ignored) {
            responseBody = connection.getErrorStream();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        responseBody.close();

        return new Gson().fromJson(response.toString(), classOfT);
    }


    private record ErrorResponse(String message) {
    }

    private record GamesList(ArrayList<GameHelper> games) {
    }

    private record JoinGameRequest(int gameID, String playerColor) {
    }
}
