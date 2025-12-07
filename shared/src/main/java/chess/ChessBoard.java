package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * removes a piece on the chessboard
     *
     * @param position
     */
    public void removePiece(ChessPosition position) {
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    public ChessPiece getPiece(int row, int col) {
        return squares[row-1][col-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        // front lines
        ChessPiece newPiece;
        ChessPosition newPosition;

        // White Pieces
        newPosition = new ChessPosition(1,8);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,7);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,6);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,5);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,4);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,3);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,2);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(1,1);
        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);

        // White Pawns
        for (int i = 1 ; i <= 8 ; i++) {
            newPosition = new ChessPosition(2, i);
            newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(newPosition, newPiece);
        }

        // Black Pieces
        newPosition = new ChessPosition(8,8);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,7);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,6);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,5);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,4);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,3);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,2);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(newPosition, newPiece);
        newPosition = new ChessPosition(8,1);
        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(newPosition, newPiece);


        // Black Pawns
        for (int i = 1 ; i <= 8 ; i++) {
            newPosition = new ChessPosition(7, i);
            newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(newPosition, newPiece);
        }
    }

    /**
     * create method that creates a copy of the original chess board to use
     */

    public ChessBoard copy(){
        ChessBoard newBoard = new ChessBoard();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece newPiece = getPiece(pos);
                if (newPiece != null){
                    newBoard.addPiece(pos, newPiece);
                }

            }
        }
        return newBoard;
    }

    public void printBoard(){
        for (int row = 8 ; row >= 1; row-- ){
            for (int col = 1 ; col <= 8; col++ ){
                ChessPiece q = getPiece(row,col);
                if (q == null){
                    System.out.print("|     ");
                }
                else{
                    System.out.print("|" + q.getPieceType());
                }

            }
            System.out.println("|");

        }
    }






    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.deepToString(squares) +
                '}';
    }
}
