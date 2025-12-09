package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameStatus;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {

    private final DatabaseManager databaseManager;
    private final Gson gson = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        try {
            this.databaseManager = new DatabaseManager();
        } catch (DataAccessException e) {
            throw new DataAccessException("Unable to initialize DatabaseManager");
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, game, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        // check duplicate
        if (getGame(gameName) != null) {
            throw new DataAccessException("Game name already taken");
        }

        ChessGame newGame = new ChessGame();
        String gameJson = gson.toJson(newGame);

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            ps.setString(4, gameJson);
            ps.setString(5, GameStatus.ACTIVE.toString());

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new DataAccessException("Game ID not returned");
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gson.fromJson(rs.getString("game"), ChessGame.class),
                            GameStatus.valueOf(rs.getString("status"))
                    );
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    private GameData getGame(String gameName) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameName=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, gameName);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gson.fromJson(rs.getString("game"), ChessGame.class),
                            GameStatus.valueOf(rs.getString("status"))
                    );
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM game";
        Collection<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gson.fromJson(rs.getString("game"), ChessGame.class),
                        GameStatus.valueOf(rs.getString("status"))
                ));
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return games;
    }

    @Override
    public void addCaller(int gameID, String playerColor, String username) throws DataAccessException {
        String sql;

        if (playerColor.equalsIgnoreCase("WHITE")) {
            sql = "UPDATE game SET whiteUsername=? WHERE gameID=?";
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            sql = "UPDATE game SET blackUsername=? WHERE gameID=?";
        } else {
            throw new DataAccessException("Player color not valid");
        }

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        String sql = "UPDATE game SET game=? WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, gson.toJson(chessGame));
            ps.setInt(2, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGameStatus(int gameID, GameStatus status) throws DataAccessException {
        String sql = "UPDATE game SET status=? WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toString());
            ps.setInt(2, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void removeWhiteUser(int gameID) throws DataAccessException {
        String sql = "UPDATE game SET whiteUsername = NULL WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void removeBlackUser(int gameID) throws DataAccessException {
        String sql = "UPDATE game SET blackUsername = NULL WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
