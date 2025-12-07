package websocket.messages;


import chess.ChessBoard;
import chess.ChessGame;

/*
Command
 - LOAD_GAME
Required Fields
 - game (can be any type, just needs to be called game)
Description
 - Used by the server to send the current game state to a client.
    When a client receives this message, it will redraw the chess board.

 */
public class LoadGameMessage extends ServerMessage{
    public ChessGame game;
    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
