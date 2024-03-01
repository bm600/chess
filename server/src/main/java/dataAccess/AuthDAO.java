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
        AuthData newAuth = new AuthData(authToken, username);
        authList.put(authToken, newAuth);
        return newAuth;
    }

    public AuthData getAuth(String authToken){
        return authList.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authList.remove(authToken);
    }
}
