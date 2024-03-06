package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO {
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
