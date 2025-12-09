package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessPosition strt, ChessPosition end, ChessPiece.PieceType piece) {
        super(commandType, authToken, gameID);
        this.move = new ChessMove(strt,end, piece);
    }

    public ChessMove getMove(){
        return move;
    }
}
