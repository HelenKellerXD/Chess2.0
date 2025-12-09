package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import model.GameStatus;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.Collection;

public class GameService {
    GameDAO gameDAO;

    public GameService() {
        try {
            gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e){
            gameDAO = new MemoryGameDAO();
            System.out.println("local Game database");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (createGameRequest.gameName() == null){
            throw new DataAccessException("Error: Bad Request");
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames() throws DataAccessException {
        Collection<GameData> listGamesResult = gameDAO.listGames();
        return new ListGamesResult(listGamesResult);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String teamColor = joinGameRequest.playerColor();


        // check -> user picked white or black
        if (teamColor == null ||
                !(teamColor.equalsIgnoreCase("WHITE") || teamColor.equalsIgnoreCase("BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        // check -> user selected actual game
        if (gameData == null) {
            throw new DataAccessException("Error: bad request");
        }

        //check -> game is active and not closed
        if (gameData.status() != GameStatus.ACTIVE) {
            throw new DataAccessException("Error: game is not joinable");
        }

        /// allowed to join if one of the users

        if (teamColor.equalsIgnoreCase("WHITE") &&
                joinGameRequest.username().equals(gameData.whiteUsername())) {
            return;
        }

        if (teamColor.equalsIgnoreCase("BLACK") &&
                joinGameRequest.username().equals(gameData.blackUsername())) {
            return;
        }




        addCaller(joinGameRequest);
    }

    public void addCaller(JoinGameRequest joinGameRequest) throws DataAccessException {
        String playerColor = joinGameRequest.playerColor();
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());

        if(playerColor.equalsIgnoreCase("WHITE")){
            if (gameData.whiteUsername() == null){
                gameDAO.addCaller(joinGameRequest.gameID(), playerColor, joinGameRequest.username());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (gameData.blackUsername() == null){
                gameDAO.addCaller(joinGameRequest.gameID(), playerColor, joinGameRequest.username());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        }
    }

    public GameData makeMove(int gameID, ChessMove mv, String playerUsername)
            throws DataAccessException {

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Error: game not found");
        }

        // Check -> if game is active
        if (gameData.status() != GameStatus.ACTIVE) {
            throw new DataAccessException("Error: game is over");
        }

        ChessGame chessGame = gameData.game();

        ChessGame.TeamColor playerColor;
        if (playerUsername.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (playerUsername.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new DataAccessException("Error: player not in this game");
        }

        if(chessGame.getTeamTurn() != playerColor){
            throw new DataAccessException("Error: not your turn");
        }

        try {
            chessGame.makeMove(mv);
        } catch (InvalidMoveException e){
            throw new DataAccessException("Error: illegal move");
        }

        GameData updatedGameData = gameData.changeGame(chessGame);

        gameDAO.updateGame(gameID, updatedGameData.game());
        return updatedGameData;
    }



    public GameData resign(int gameID, String username) throws DataAccessException {
        GameData cur = gameDAO.getGame(gameID);

        if (cur.status() == GameStatus.OVER) {
            throw new DataAccessException("Error: game already over");
        }

        // only players can resign
        if (!username.equals(cur.whiteUsername()) && !username.equals(cur.blackUsername())) {
            throw new DataAccessException("Error: observers cannot resign");
        }

        gameDAO.updateGameStatus(cur.gameID(), GameStatus.OVER);
        return gameDAO.getGame(gameID);
    }

    public GameData leave(int gameID, String username) throws DataAccessException {
        GameData cur = gameDAO.getGame(gameID);

        if (cur.status() == GameStatus.OVER) {
            throw new DataAccessException("Error: game already over");
        }

        // only players can resign
        if (username.equals(cur.whiteUsername())) {
            gameDAO.removeWhiteUser(gameID);

        }
        if (username.equals(cur.blackUsername())) {
            gameDAO.removeBlackUser(gameID);
        }

        return gameDAO.getGame(gameID);
    }



    public GameData getGame(int gameID) throws DataAccessException {
        GameData gmData = gameDAO.getGame(gameID);
        if (gmData == null){
            throw new DataAccessException("Error: game not found");
        }
        return gmData;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }


}
