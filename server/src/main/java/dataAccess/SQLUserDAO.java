package dataAccess;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO extends SQLDAO implements UserDAO{

    private static final String TABLE = "user";

    static private final String[] createStatements = {
            String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `username` varchar(256) NOT NULL,
                        `password` varchar(256) NOT NULL,
                        `email` varchar(256) NOT NULL,
                        PRIMARY KEY (`id`),
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

    public void deleteAllUsers() throws DataAccessException {
        var statement = String.format("TRUNCATE %s", TABLE);
        executeUpdate(statement);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var us_statement = String.format("SELECT password FROM %s WHERE username=?", TABLE);
            try (var us = conn.prepareStatement(us_statement)) {
                us.setString(1, username);
                try (var rs = us.executeQuery()) {
                    if (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public SQLUserDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public UserData createUser(UserData userData) throws DataAccessException {
        var username = userData.getUsername();
        var password = userData.getPassword();
        var email = userData.getEmail();

        //Password Encryption
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        var statement = String.format("INSERT INTO %s (username, password, email) VALUES (?, ?, ?)", TABLE);
        executeUpdate(statement, username, hashedPassword, email);
        return userData;
    }

}