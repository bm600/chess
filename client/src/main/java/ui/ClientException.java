package ui;

public class ClientException extends Exception {

    public ClientException(int statusCode, String message) {
        super(message);
    }

}