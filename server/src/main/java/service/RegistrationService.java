package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class RegistrationService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    public UserData createUser(UserData userData){
        if (userData.getPassword() == null || userData.getEmail() == null || userData.getUsername() == null)
            throw new IllegalArgumentException("Invalid user data"); //TODO change exception
        return userDAO.createUser(userData);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        return authDAO.createAuth(username);
    }
}
