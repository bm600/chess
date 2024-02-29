package service;

import dataAccess.AuthDAO;
import model.AuthData;

public class LogoutService {
    private AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken){
        authDAO.deleteAuth(authToken);
    }
}
