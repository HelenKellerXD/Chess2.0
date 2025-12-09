package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameStatus;

import java.util.Collection;

/**
 * run CRUD on this
 *  - create
 *  - read (get/find)
 *  - update
 *  - delete
 */
public interface GameDAO {

    /**
     * createGame: Create a new game.
     */
    int createGame(String gameName) throws DataAccessException;

    /**
     * getGame: Retrieve a specified game with the given game ID.
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * listGames: Retrieve all games.
     */
    Collection<GameData> listGames() throws DataAccessException;

    /**
     * addCaller: Assigns a user to WHITE or BLACK in the game.
     */
    void addCaller(int gameID, String playerColor, String userName) throws DataAccessException;

    /**
     * updateGame:
     * Updates the ChessGame object (board state, turn, etc.)
     */
    void updateGame(int gameID, ChessGame chessGame) throws DataAccessException;

    /**
     * NEW: updateGameStatus:
     * - Updates whether a game is ACTIVE, WHITE_RESIGNED, BLACK_RESIGNED, or FINISHED.
     * - Used when someone resigns or the game ends.
     */
    void updateGameStatus(int gameID, GameStatus status) throws DataAccessException;

    public void removeWhiteUser(int gameID) throws DataAccessException;
    public void removeBlackUser(int gameID) throws DataAccessException;
    public void removeUser(int gameID, String color) throws DataAccessException;


        /**
         * clearGames: deletes all games from the database.
         */
    void clear() throws DataAccessException;
}
