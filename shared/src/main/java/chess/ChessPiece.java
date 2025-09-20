package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    //object to store possible moves for piece
    public Collection<ChessMove> movesList = new ArrayList<>();


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() { return pieceColor;}

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }


    /**
     * Helper functions
     *  - onBoard check
     *  - friendly piece check
     *  - empty check
     *  - recursion steps
     */


    // Board check - check to see if position is still on the board
    public boolean onBoard(ChessPosition position){
        return position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() <= 8 && position.getColumn() <= 8;
    };
    // Opposing piece check
    public boolean isEnemy(ChessBoard board, ChessPosition position){ return board.getPiece(position).getTeamColor() != pieceColor; };
    // Empty space check
    public boolean isEmpty(ChessBoard board, ChessPosition position){ return board.getPiece(position) == null; };
    //RECURSION function
    public void recuringMovement(ChessBoard board, ChessPosition start, ChessPosition newPosition, int rowMove, int colMove){
        // update newPosition using the row and col move numbers
        newPosition = newPosition.moveDirection(rowMove, colMove);
        // check to see if the new space is on the board
        if (onBoard(newPosition)) {
            // check to see if the new space is empty
            if(isEmpty(board, newPosition)) {
                // if true -> add to moveslist and call recursion
                ChessMove newMove = new ChessMove(start, newPosition, null);
                movesList.add(newMove);
                recuringMovement(board,start,newPosition,rowMove,colMove);
            }
            else {
                // if false-> check to see if occupying piece is enemy
                if (isEnemy(board,newPosition)) {
                    // -> if enemy -> add position to moveslist
                    ChessMove newMove = new ChessMove(start, newPosition, null);
                    movesList.add(newMove);
                }
                // -> if friendly -> do NOT add to moveslist
            }

            //
        }

    };

    // moves only once
    public void validMovement(ChessBoard board, ChessPosition start, ChessPosition newPosition, int rowMove, int colMove){
        // update newPosition using the row and col move numbers
        newPosition = newPosition.moveDirection(rowMove, colMove);
        // check to see if the new space is on the board
        if (onBoard(newPosition)) {
            // check to see if the new space is empty
            if(isEmpty(board, newPosition)) {
                // if true -> add to moveslist
                ChessMove newMove = new ChessMove(start, newPosition, null);
                movesList.add(newMove);
            }
            else {
                // if false-> check to see if occupying piece is enemy
                if (isEnemy(board,newPosition)) {
                    // -> if enemy -> add position to moveslist
                    ChessMove newMove = new ChessMove(start, newPosition, null);
                    movesList.add(newMove);
                }
                // -> if friendly -> do NOT add to moveslist
            }

            //
        }

    };

    public void advancePawn(ChessPosition start, ChessPosition location){
        //add each piece as a promotion piece
        ChessMove newMove = new ChessMove(start, location, PieceType.QUEEN);
        movesList.add(newMove);
        newMove = new ChessMove(start, location, PieceType.BISHOP);
        movesList.add(newMove);
        newMove = new ChessMove(start, location, PieceType.KNIGHT);
        movesList.add(newMove);
        newMove = new ChessMove(start, location, PieceType.ROOK);
        movesList.add(newMove);
    }

    // boolean to check it the pawn is at the start
    public boolean pawnStart(ChessPosition location){
        // return true if the row for the pawn is 2 for white or 7 for black
        return (pieceColor == ChessGame.TeamColor.WHITE && location.getRow() == 2)
                || (pieceColor == ChessGame.TeamColor.BLACK && location.getRow() == 7);
    }


    // pawn foward movement - just like validmovement except you can't take the spot of pieces in front and can only move one direction
    public void pawnForward(ChessBoard board, ChessPosition start, ChessPosition newPosition){
        // update newPosition using the row and col move numbers
        int rowMove;
        int colMove;

        if (pieceColor == ChessGame.TeamColor.WHITE){
            rowMove = 1;
            colMove = 0;
        }
        else{
            rowMove = -1;
            colMove = 0;
        }
        newPosition = newPosition.moveDirection(rowMove, colMove);
        // check to see if the new space is on the board
        if (onBoard(newPosition)) {
            // check to see if the new space is empty
            if(isEmpty(board, newPosition)) {
                // if true -> add to moveslist
                // check to see if new position is on the front or back of the board and advance if so
                if(newPosition.getRow() == 1 || newPosition.getRow() == 8){
                    advancePawn(start, newPosition);
                }
                else {
                    ChessMove newMove = new ChessMove(start, newPosition, null);
                    movesList.add(newMove);
                }
                // if pawnstart is true, the run pawnForward again
                if (pawnStart(newPosition.moveDirection(0-rowMove,0))) {
                    pawnForward( board,  start,  newPosition);
                }

            }
            // -> enemy or friendly -> do NOT add to moveslist

            //
        }

    };

    // diagonal check function
    public void diagonalStepIn(ChessBoard board, ChessPosition start, ChessPosition newPosition, int rowMove, int colMove){
        newPosition = start.moveDirection(rowMove, colMove);
        // check to see if the new space is on the board
        if (onBoard(newPosition)) {
            // check to see if the new space is empty
            if(isEmpty(board, newPosition)) {
                // if true -> add to do NOT ADD

            }
            else {
                // if false-> check to see if occupying piece is enemy
                if (isEnemy(board,newPosition)) {
                    // -> if enemy -> add position to moveslist
                    // check to see if new position is on the front or back of the board and advance if so
                    if(newPosition.getRow() == 1 || newPosition.getRow() == 8){
                        advancePawn(start, newPosition);
                    }
                    else {
                        ChessMove newMove = new ChessMove(start, newPosition, null);
                        movesList.add(newMove);
                    }
                }
                // -> if friendly -> do NOT add to moveslist
            }

            //
        }
    }

    // pawn diagnol movement - figures out pawns color and then calls valid movement for specific direction
    public void pawnDiagnol(ChessBoard board, ChessPosition start, ChessPosition newPosition){
        // update newPosition using the row and col move numbers
        int rowMove;
        int colMoveRight = 1;
        int colMoveLeft = -1;

        // figure out the direction of the pawn
        if (pieceColor == ChessGame.TeamColor.WHITE){
            rowMove = 1;
        }
        else{
            rowMove = -1;
        }
        // do diagonalStepIn for both directions
        diagonalStepIn(board, start, newPosition, rowMove, colMoveLeft);
        diagonalStepIn(board, start, newPosition, rowMove, colMoveRight);
    };





    /**
     * Create the movement classes for each piece
     */
    public void movementBishop(ChessBoard board, ChessPosition myPosition){
        //call recursion function in each diaganol direction (1,1)(1,-1)(-1,-1)(-1,1)
        recuringMovement(board, myPosition, myPosition,1,1);
        recuringMovement(board, myPosition, myPosition,1,-1);
        recuringMovement(board, myPosition, myPosition,-1,-1);
        recuringMovement(board, myPosition, myPosition,-1,1);
    }
    public void movementRook(ChessBoard board, ChessPosition myPosition){
        //call recursion function in each straight direction (1,0)(-1,0)(0,-1)(0,1)
        recuringMovement(board, myPosition, myPosition,1,0);
        recuringMovement(board, myPosition, myPosition,0,-1);
        recuringMovement(board, myPosition, myPosition,-1,0);
        recuringMovement(board, myPosition, myPosition,0,1);
    }
    public void movementQueen(ChessBoard board, ChessPosition myPosition){
        //call movementRook and movementBishop
        movementBishop(board, myPosition);
        movementRook(board, myPosition);
    }
    public void movementKnight(ChessBoard board, ChessPosition myPosition){
        //call validMovement function in each direction (-1,2)(1,2)(2,1)(2,-1)(-1,-2)(-1,-2)(-2,-1)(-2,1)
        validMovement(board, myPosition, myPosition,-1,-2);
        validMovement(board, myPosition, myPosition,-1,2);
        validMovement(board, myPosition, myPosition,1,-2);
        validMovement(board, myPosition, myPosition,1,2);
        validMovement(board, myPosition, myPosition,-2,-1);
        validMovement(board, myPosition, myPosition,-2,1);
        validMovement(board, myPosition, myPosition,2,-1);
        validMovement(board, myPosition, myPosition,2,1);
    }

    public void movementKing(ChessBoard board, ChessPosition myPosition){
        //call validMovement function 1 space all around
        validMovement(board, myPosition, myPosition,1,-1);
        validMovement(board, myPosition, myPosition,1,0);
        validMovement(board, myPosition, myPosition,1,1);
        validMovement(board, myPosition, myPosition,-1,-1);
        validMovement(board, myPosition, myPosition,-1,0);
        validMovement(board, myPosition, myPosition,-1,1);
        validMovement(board, myPosition, myPosition,0,-1);
        validMovement(board, myPosition, myPosition,0,1);
    }




    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        movesList = new ArrayList<>();
        switch(type){
            case BISHOP:
                movementBishop(board,myPosition);
                break;
            case ROOK:
                movementRook(board,myPosition);
                break;
            case QUEEN:
                movementQueen(board,myPosition);
                break;
            case KNIGHT:
                movementKnight(board,myPosition);
                break;
            case KING:
                movementKing(board,myPosition);
                break;
            case PAWN:
                pawnForward(board,myPosition,myPosition);
                pawnDiagnol(board, myPosition,myPosition);
                break;
        }
        return movesList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, movesList);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                ", movesList=" + movesList +
                '}';
    }
}
