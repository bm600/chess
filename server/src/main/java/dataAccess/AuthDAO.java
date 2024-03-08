package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void deleteAllAuth() throws DataAccessException;

    AuthData createAuth(String username, String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
