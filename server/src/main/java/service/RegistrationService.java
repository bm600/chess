package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import util.AuthTokenGenerator;

public class RegistrationService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegistrationService(UserDAO userDAO, AuthDAO authDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }
    public void createUser(UserData userData) throws DataAccessException {
        if (userData.getPassword() == null || userData.getEmail() == null || userData.getUsername() == null)
            throw new IllegalArgumentException("Invalid user data");
        userDAO.createUser(userData);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if(userDAO.getUser(username) == null){
            throw new DataAccessException("No User Found");
        }
        String authToken = AuthTokenGenerator.makeToken();
        return authDAO.createAuth(username, authToken);
    }
    public String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
