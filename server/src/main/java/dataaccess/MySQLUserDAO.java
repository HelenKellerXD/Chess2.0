package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{

    //@Language("SQL")


    public MySQLUserDAO() throws DataAccessException {
        try {
            new DatabaseManager();
        } catch (DataAccessException e) {
            throw new DataAccessException("Unable to create Database");
        }
    }


    @Override
    public void createUser(String username, String password, String email) throws DataAccessException{

        // check to see if username is already taken and throw exception if true
        if (getUser(username) != null){
            throw new DataAccessException("Username already taken");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?,?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,username);
                preparedStatement.setString(2,hashedPassword);
                preparedStatement.setString(3,email);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String userNm = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");

                        return new UserData(userNm,password,email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM user";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }
}
