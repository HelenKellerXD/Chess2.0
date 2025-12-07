package dataaccess;

import chess.ChessGame;
import model.GameData;

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
     * glistGames: Retrieve all games.
     */

    Collection<GameData> listGames() throws DataAccessException;

    /**
     * updateGame:
     * - Updates a chess game. It should replace the chess game string corresponding to a given gameID.
     * - This is used when players join a game or when a move is made.
     */
    void addCaller(int gameID, String playerColor, String userName) throws DataAccessException;


    ///  update game
    void updateGame(int gameID, ChessGame chessGame) throws DataAccessException;

    /**
     * clearGames: deletes all games from the database
     */
    void clear() throws DataAccessException;


}
