package service;

import dataAccess.MemoryAuthDAO;
import model.AuthData;

public class LogoutService {
    private final MemoryAuthDAO authDAO;

    public LogoutService(MemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken){
        authDAO.deleteAuth(authToken);
    }
}
