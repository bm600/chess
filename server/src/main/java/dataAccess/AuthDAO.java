package dataAccess;

import model.AuthData;
import util.AuthTokenGenerator;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private static Map<String, AuthData> authList = new HashMap<String, AuthData>();

    public void deleteAllAuth() {
        authList.clear();
    }

    public AuthData createAuth(String username) {
        String authToken = AuthTokenGenerator.makeToken();
        AuthData new_auth = new AuthData(authToken, username);
        authList.put(authToken, new_auth);
        return new_auth;
    }
}
