package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
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
            //System.out.println("SQL Game database");
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
    public ListGamesResult listGames(Object object) throws DataAccessException {
        throw new DataAccessException("Error: object passed in to listGames as a parameter");
    }

    public ListGamesResult listGames() throws DataAccessException {
        Collection<GameData> listGamesResult = gameDAO.listGames();
        return new ListGamesResult(listGamesResult);
    }

    public void addCaller(JoinGameRequest joinGameRequest) throws DataAccessException {
        String playerColor = joinGameRequest.playerColor();
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if(playerColor.equalsIgnoreCase("WHITE")){
            if (gameData.whiteUsername() == null){
                gameDAO.addCaller(joinGameRequest.gameID(), playerColor, joinGameRequest.username());
            }
            else {
                throw new DataAccessException("Error: already taken");
            }
        }
        else{
            if (gameData.blackUsername() ==  null){
                gameDAO.addCaller(joinGameRequest.gameID(), playerColor, joinGameRequest.username());
            }
            else {
                throw new DataAccessException("Error: already taken");
            }
        }

    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String teamColor = joinGameRequest.playerColor();
        if (teamColor == null){
            throw new DataAccessException("Error: bad request");
        }
        if (teamColor.equalsIgnoreCase("WHITE") || teamColor.equalsIgnoreCase("BLACK")){

            if(gameDAO.getGame(joinGameRequest.gameID()) != null){
                addCaller(joinGameRequest);
            }
            else{
                throw new DataAccessException("Error: bad request");
            }
        }
        else{
            throw new DataAccessException("Error: bad request");
        }
    }

    public GameData makeMove(int gameID, ChessMove mv, String playerUsrname) throws DataAccessException, InvalidMoveException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Error: game not found");
        }

        /// you're checking the player with the gameData in order to
        /// determine what team the requested player will move as well as if the
        ///  request was even made by a player
        ChessGame chessGame = gameData.game();

        ChessGame.TeamColor playerColor;
        if (playerUsrname.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (playerUsrname.equals(gameData.blackUsername())) {
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

    public GameData getGame(int gameID) throws DataAccessException {
        try{
            GameData gmData = gameDAO.getGame(gameID);
            if (gmData == null){
                throw new DataAccessException("Error: game not found");
            }
            return gmData;
        } catch (DataAccessException ex){
            throw ex;

        }catch(Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }




        public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
