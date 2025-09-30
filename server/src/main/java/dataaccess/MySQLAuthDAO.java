package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;


public class MySQLAuthDAO implements AuthDAO{

    private DatabaseManager databaseManager;

    


    public MySQLAuthDAO() throws DataAccessException {
        try {
            new DatabaseManager();
        } catch (DataAccessException e) {
            throw new DataAccessException("Unable to create Database");
        }
    }

    @Override
    public String createAuth(String userName) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,token);
                preparedStatement.setString(2,userName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth");
        }

        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to access auth");
        }
        return null;


    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }



}
