package dataAccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class UserDAO {
    private static HashMap<String, UserData> userList = new HashMap<String, UserData>();

    public void deleteAllUsers() {
        userList.clear();
    }

    public UserData getUser(String username){
        return userList.get(username);
    }

    public UserData createUser(UserData userData) {
        userList.put(userData.getUsername(), userData);
        return userData;
    }
}
