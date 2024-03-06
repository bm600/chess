package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

public class ClearService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;

    public ClearService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO){
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
