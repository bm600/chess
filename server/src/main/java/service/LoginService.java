package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public LoginService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        return authDAO.createAuth(username);
    }
}
