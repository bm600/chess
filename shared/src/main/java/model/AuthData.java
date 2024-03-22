package model;

import java.util.Objects;
import java.util.UUID;

public class AuthData {
    String authToken;
    String username;

    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public AuthData(String username){
        this.authToken = UUID.randomUUID().toString();
        this.username = username;

    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return Objects.equals(authToken, authData.authToken) && Objects.equals(username, authData.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
