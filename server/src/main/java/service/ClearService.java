package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    public void clearAll(){
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
