package dataAccess;

import model.UserData;

import java.util.HashMap;

public interface UserDAO {

    void deleteAllUsers() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData createUser(UserData userData) throws DataAccessException;
}
