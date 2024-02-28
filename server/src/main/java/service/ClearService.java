package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    void clearApplication(){
        deleteAllUsers();
        deleteAllGames();
    }
    public void deleteAllUsers(){
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuth();
    }
    public void deleteAllGames(){
        gameDAO.deleteAllGames();
    }
}
