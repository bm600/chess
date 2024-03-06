package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class RegistrationService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public RegistrationService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }
    public void createUser(UserData userData){
        if (userData.getPassword() == null || userData.getEmail() == null || userData.getUsername() == null)
            throw new IllegalArgumentException("Invalid user data");
        userDAO.createUser(userData);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        return authDAO.createAuth(username);
    }
}
