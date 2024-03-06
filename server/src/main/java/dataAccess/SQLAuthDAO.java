package dataAccess;

import model.AuthData;
import util.AuthTokenGenerator;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{

    //TODO make db table in static initializer (auth) table values are authToken and userName
    @Override
    public void deleteAllAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = AuthTokenGenerator.makeToken();
        AuthData newAuth = new AuthData(authToken, username);
        var statement = "INSERT INTO auth (authToken, userName) VALUES (?, ?)";
        executeUpdate(statement, newAuth.getAuthToken(), newAuth.getUsername());
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
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
                var statement = "DELETE FROM auth WHERE authToken=?";
                executeUpdate(statement, authToken);
            }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {

    }
}
