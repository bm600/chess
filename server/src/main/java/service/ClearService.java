package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

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
