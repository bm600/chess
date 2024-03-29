package dataAccess;

import model.AuthData;

import java.sql.*;

public class SQLAuthDAO extends SQLDAO implements AuthDAO{

    private static final String TABLE = "auth";

    static private final String[] createStatements = {
            String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        `username` varchar(256),
                        `authToken` varchar(256),
                        PRIMARY KEY (`authToken`),
                        INDEX(username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """, TABLE)
    };

    static {
        try {
            configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = String.format("TRUNCATE %s", TABLE);
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username, String authToken) throws DataAccessException {
        AuthData newAuth = new AuthData(authToken, username);
        var statement = String.format("INSERT INTO %s (authToken, username) VALUES (?, ?)", TABLE);
        executeUpdate(statement, newAuth.getAuthToken(), newAuth.getUsername());
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = String.format("SELECT username FROM %s WHERE authToken=?", TABLE);
            try (var au = conn.prepareStatement(statement)) {
                au.setString(1, authToken);
                try (var rs = au.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = String.format("DELETE FROM %s WHERE authToken=?", TABLE);
        executeUpdate(statement, authToken);
    }
}
