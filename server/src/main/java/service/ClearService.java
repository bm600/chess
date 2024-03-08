package service;

import dataAccess.*;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    public void clearAll() throws DataAccessException {
        deleteAllUsers();
        deleteAllGames();
    }
    public void deleteAllUsers() throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuth();
    }
    public void deleteAllGames() throws DataAccessException {
        gameDAO.deleteAllGames();
    }

}
