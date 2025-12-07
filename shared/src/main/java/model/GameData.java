package model;

import chess.ChessBoard;
import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
    public GameData addBlackPlayer(String newBlackUsername){
        return new GameData(gameID, whiteUsername,newBlackUsername,gameName,game);
    }
    public GameData addWhitePlayer(String newWhiteUsername){
        return new GameData(gameID, newWhiteUsername,blackUsername,gameName,game);
    }
    public GameData changeGame(ChessGame game){
        return new GameData(gameID, whiteUsername,blackUsername,gameName,game);
    }
}
