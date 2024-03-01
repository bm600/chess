package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class RegistrationService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegistrationService(UserDAO userDAO, AuthDAO authDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }//TODO possibly delete this function from here later
    public void createUser(UserData userData){
        if (userData.getPassword() == null || userData.getEmail() == null || userData.getUsername() == null)
            throw new IllegalArgumentException("Invalid user data"); //TODO change exception
        userDAO.createUser(userData);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        return authDAO.createAuth(username);
    }
}
