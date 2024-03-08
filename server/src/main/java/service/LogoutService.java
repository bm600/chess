package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }
}
