package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        return authDAO.createAuth(username);
    }
}
