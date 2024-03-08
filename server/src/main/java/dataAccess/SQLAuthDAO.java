package dataAccess;

import model.AuthData;
import util.AuthTokenGenerator;

import java.sql.*;
import dataAccess.DatabaseManager.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO extends SQLDAO implements AuthDAO{

    private static final String TABLE = "auth";

    static private final String[] createStatements = {
            String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        `username` varchar(256) NOT NULL,
                        `authToken varchar(256) NOT NULL,
                        PRIMARY KEY (`authToken`),
                        INDEX(username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """, TABLE)
    };

    //TODO this static block may not work so check up on it fr fr
    static {
        try {
            configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = "TRUNCATE %s";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = AuthTokenGenerator.makeToken();
        AuthData newAuth = new AuthData(authToken, username);
        var statement = "INSERT INTO %s (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, newAuth.getAuthToken(), newAuth.getUsername());
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM %s WHERE authToken=?";
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
        var statement = "DELETE FROM %s WHERE authToken=?";
        executeUpdate(statement, authToken);
    }
}
