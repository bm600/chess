import java.util.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.*;
import ui.DrawChessboard;
import ui.ClientException;

public class Main {
    private static ServerFacade server;
    private static AuthData userAuth = null;

    public static void main(String[] args) {
        server = new ServerFacade("localhost", 8080);
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Welcome to Chess Game! Get ready to play.");
        boolean gameActive = true;

        while (gameActive) {
            displayLoginStatus();

            String userInput = inputScanner.nextLine();
            String[] commandInputs = userInput.split("\\s+");
            String action = commandInputs[0].toLowerCase();
            String[] actionParams = Arrays.copyOfRange(commandInputs, 1, commandInputs.length);

            try {
                executeAction(action, actionParams);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        inputScanner.close();
    }

    private static void displayLoginStatus() {
        System.out.print((userAuth == null ? "Logged-Out" : "Logged-In") + " >>> ");
    }

    private static void executeAction(String action, String[] params) throws Exception {
        switch (action) {
            case "register":
                performRegistration(params);
                break;
            case "login":
                performLogin(params);
                break;
            case "logout":
                performLogout();
                break;
            case "list":
                displayAvailableGames();
                break;
            case "create":
                initiateGameCreation(params);
                break;
            case "join":
                participateInGame(params);
                break;
            case "observe":
                observeOngoingGame(params);
                break;
            case "quit":
                System.out.println("Exiting game...");
                System.exit(0);
                break;
            case "help":
            default:
                showCommandHelp();
                break;
        }
    }

    private static void performRegistration(String[] params) throws Exception {
        if (params.length != 3) {
            System.out.println("Usage: register <username> <password> <email>");
            return;
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        try {
            UserData newUser = new UserData(username, password, email);
            userAuth = server.register(newUser);
            System.out.println("Registration successful. You are now logged in.");
        } catch (ClientException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void performLogin(String[] params) throws Exception {
        if (params.length != 2) {
            System.out.println("Usage: login <username> <password>");
            return;
        }

        String username = params[0];
        String password = params[1];

        try {
            UserData user = new UserData(username, password, "");
            userAuth = server.login(user);
            System.out.println("Login successful.");
        } catch (ClientException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void performLogout() throws Exception {
        if (userAuth == null) {
            System.out.println("You are not logged in.");
            return;
        }

        try {
            server.logout(userAuth);
            userAuth = null;
            System.out.println("Logout successful.");
        } catch (ClientException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private static void displayAvailableGames() throws Exception {
        if (userAuth == null) {
            System.out.println("Please login to view available games.");
            return;
        }

        try {
            List<GameData> gamesList = server.listGames(userAuth);
            if (gamesList.isEmpty()) {
                System.out.println("No games available at the moment.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < gamesList.size(); i++) {
                    String gameName = (i + 1) + ": " + gamesList.get(i).getGameName();
                    String whitePlayer = gamesList.get(i).getWhiteUsername();
                    String blackPlayer = gamesList.get(i).getBlackUsername();

                    String whiteStatus = (whitePlayer == null || whitePlayer.isEmpty()) ? "WHITE: no players" : STR."WHITE: \{whitePlayer}";
                    String blackStatus = (blackPlayer == null || blackPlayer.isEmpty()) ? "BLACK: no players" : STR."BLACK: \{blackPlayer}";

                    if (whiteStatus.equals("WHITE: no players") && blackStatus.equals("BLACK: no players")) {
                        System.out.println(gameName + ": no players");
                    } else {
                        System.out.println(gameName + ": " + whiteStatus + " " + blackStatus);
                    }
                }
            }
        } catch (ClientException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private static void initiateGameCreation(String[] params) throws Exception {
        if (userAuth == null) {
            System.out.println("Please login to create a game.");
            return;
        }

        if (params.length < 1) {
            System.out.println("Usage: create <gameName>");
            return;
        }

        try {
            String gameName = String.join(" ", params);
            server.createGame(userAuth, gameName);
            System.out.println("Game created successfully.");
        } catch (ClientException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private static void participateInGame(String[] params) throws Exception {
        if (userAuth == null) {
            System.out.println("Please login to join a game.");
            return;
        }

        if (params.length != 2) {
            System.out.println("Usage: join <gameID> <playerColor>");
            return;
        }

        try {
            int gameId = Integer.parseInt(params[0]);
            String playerColor = params[1];
            server.join(userAuth, gameId, playerColor);
            DrawChessboard.draw();
            DrawChessboard.drawReverse();
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID.");
        } catch (ClientException e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private static void observeOngoingGame(String[] params) throws Exception {
        if (userAuth == null) {
            System.out.println("Please login to observe a game.");
            return;
        }

        if (params.length != 1) {
            System.out.println("Usage: observe <gameID>");
            return;
        }

        try {
            int gameId = Integer.parseInt(params[0]);
            if (server.observe(userAuth, gameId)) {
                DrawChessboard.draw();
                DrawChessboard.draw();
                System.out.println("Observed game successfully.");
            } else {
                System.out.println("Game not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID.");
        } catch (ClientException e) {
            System.out.println("Failed to observe game: " + e.getMessage());
        }
    }

        private static void showCommandHelp() {
            if (userAuth == null) {
                System.out.println("Available commands:\nregister <username> <password> <email>\nlogin <username> <password>\nhelp\nquit");
            } else {
                System.out.println("Available commands:\nlogout\nlist\ncreate <name>\njoin\nobserve\nquit");
            }
        }
    }


