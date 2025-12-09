package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameDB = new HashMap<>();


    @Override
    public int createGame(String gameName) {
        int gameID = Math.abs(UUID.randomUUID().hashCode() % 40);

        while(gameDB.containsKey(gameID)){
            gameID += 1;
        }

        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(gameID, null,null, gameName, chessGame, GameStatus.ACTIVE);
        gameDB.put(gameID, newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        if(gameDB.containsKey(gameID)) {
            return gameDB.get(gameID);
        }
        else{
            return null;
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return gameDB.values();
    }

    @Override
    public void addCaller(int gameID, String playerColor, String username) {
        if(playerColor.equalsIgnoreCase("WHITE")){
            gameDB.replace(gameID, getGame(gameID).addWhitePlayer(username));
        }
        else{
            gameDB.replace(gameID,getGame(gameID).addBlackPlayer(username));

        }

    }

    @Override
    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
    }

    @Override
    public void updateGameStatus(int gameID, GameStatus status) throws DataAccessException {

    }

    @Override
    public void removeWhiteUser(int gameID) throws DataAccessException {

    }

    @Override
    public void removeBlackUser(int gameID) throws DataAccessException {

    }

    @Override
    public void removeUser(int gameID, String color) throws DataAccessException {

    }


    @Override
    public void clear() {
        gameDB.clear();
    }
}
