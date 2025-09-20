package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * function swaps the team color
     */
    public void switchTurns(){
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return validMoves(startPosition, board);
    }

    // returns a collection of moves that have been checked for everything
    public Collection<ChessMove> validMoves(ChessPosition startPosition, ChessBoard chessBoard) {
        Collection<ChessMove> result = new ArrayList<>();
        if (chessBoard.getPiece(startPosition) != null) {
            return protectTheKing(startPosition );
        }
        return new ArrayList<>();
    }


    // gives me the set of moves for one piece
    public Collection<ChessMove> moveSet (TeamColor teamColor, ChessBoard chessBoard, ChessPosition piecePosition){
        Collection<ChessMove> moves = new ArrayList<>();
        // check if the spot is not null
        ChessPiece mysteryPiece = chessBoard.getPiece(piecePosition);
        if (mysteryPiece != null){
            // check to see if the piece is the wanted team
            if (mysteryPiece.getTeamColor() == teamColor) {
                //run validMoves on the piece
                Collection<ChessMove> newMoves = mysteryPiece.pieceMoves(chessBoard,piecePosition);
                // and add those moves to the moves list
                moves.addAll(newMoves);
            }
        }
        return moves;
    }


    /**
     * function gets a list of all possible moves of all chess pieces and returns a collection of all the moves
     */
    public Collection<ChessMove> allMoves(TeamColor teamColor, ChessBoard chessBoard){
        Collection<ChessMove> moves = new ArrayList<>();
        //get all possible moves (move.getEndPosition()) from one team
        for (int i = 1 ; i <= 8; i++){
            for (int j = 1 ; j <=8; j++){
                ChessPosition piecePosition = new ChessPosition(i,j);
                moves.addAll(moveSet(teamColor, chessBoard, piecePosition));

            }
        }

        return moves;
    }

    /**
     * function takes a list of moves and just returns a list of the possible final positions of moves
     */
    public Collection<ChessPosition> endMoves(ChessPosition attacker){
        return endMoves(attacker, board);
    }

    public Collection<ChessPosition> endMoves(ChessPosition attacker, ChessBoard chessBoard){
        Collection<ChessPosition> attacks = new ArrayList<>();
        ChessPiece chessPiece = chessBoard.getPiece(attacker);
        if (chessPiece != null) {
            Collection<ChessMove> fullAttacks = chessBoard.getPiece(attacker).pieceMoves(chessBoard, attacker);
            for (var i : fullAttacks) {
                attacks.add(i.getEndPosition());
            }
        }
        return attacks;
    }


    /**
     * make a movePiece function
     */
    public void movePiece(ChessBoard newBoard,ChessMove move){
        ChessPiece movingPiece = newBoard.getPiece(move.getStartPosition());

        //watch out for promotion pieces
        if (move.getPromotionPiece() != null){
            movingPiece = new ChessPiece(movingPiece.getTeamColor(),move.getPromotionPiece());
        }
        newBoard.addPiece(move.getEndPosition(),movingPiece);
        newBoard.removePiece(move.getStartPosition());
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // check to see if there is a piece for move
        if (board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("Invalid move: " + move);
        }

        //check to see if piece being moved is out of turn
        if (board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Invalid move: " + move);
        }

        //check first to see if the piece at move.start has a valid move that matches the move.end
        Collection<ChessMove> validMove = new ArrayList<>();
        validMove = validMoves(move.getStartPosition());
        //filter out any moves that would result in the king entering check
        validMove = protectTheKing(board.getPiece(move.getStartPosition()).getTeamColor());



        if (validMove.contains(move)){
            validMove = new ArrayList<>();
            // -> if true: call movePiece() to add the chess piece to end location and then delete it from starting location
            movePiece(board, move);
            // switch color to the other team
            switchTurns();
            board.printBoard();

        }
        else {
            // -> if not, throw error
            throw new InvalidMoveException("Invalid move: " + move);
        }

    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }


    public boolean isInCheck(TeamColor teamColor, ChessBoard nextBoard) {
        Collection<ChessPosition> attacks = new ArrayList<>();
        ChessPosition kingsPosition = null;
        //get kings position
        //get all possible moves (move.getEndPosition()) from all opposing team pieces
        Collection<ChessMove> possibleMoves = allMoves(teamColor, nextBoard);

        for (int i = 1 ; i <= 8; i++){
            for (int j = 1 ; j <=8; j++){
                ChessPosition newPosition = new ChessPosition(i,j);
                // check if the spot is not null
                ChessPiece mysteryPiece = nextBoard.getPiece(newPosition);
                if (mysteryPiece != null){
                    //check if the piece is the king of teamcolor
                    if (mysteryPiece.getTeamColor() == teamColor && mysteryPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        //if true -> save to kings position
                        kingsPosition = newPosition;
                    }
                    else if (mysteryPiece.getTeamColor() != teamColor) {
                        // if false -> check to see if the piece is not teamcolor
                        attacks.addAll(endMoves(newPosition, nextBoard));
                        //if true -> save all the enemy pieces possible endmoves to attacks
                    }
                }
            }
        }
        //if the king is found in any of the end moves, then the king is in check and return turn
        return (attacks.contains(kingsPosition));
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // check first to see if king is even in check
        if (!isInCheck(teamColor)){
            return false;
        }

        // look to see if there are any valid moves to get the king out of check
        Collection<ChessMove> movesList = allMoves(teamColor, board);
        movesList = protectTheKing(teamColor);
        return movesList.isEmpty();
    }

    /**
     * check the enemy team
     */



    /**
     * Create function that looks at possible moves that keeps the king from being in check
     * -> check to see if the king can move out of check and return those moves
     * -> check to see what piece has put the king in check
     * -> -> check to see if any piece can block off or eliminate the piece that has the king in check
     * -> -> -> check to see if by moving saving piece, if the king will end up in check from another piece
     * -> return moves list
     */

    /**
     * Create a function that if the king is in check then only return the list of valid moves that get the king out of check
     */

    /**
     * create a function that iterates through the valid moves list and checks to see if any of move options would put the king in check
     * -> if the move does, then remove it from the valid moves list
     */

    // iterate through the list of attacks and create a board that shows the move at the new location
    // create a new board that has the piece moved
    // check to see if the king isInCheck() on the new board
    // if the king is in check, remove that move from the Collection of attacks
    // UPDATE: instead, return a list of valid attacks instead of removing

    public Collection<ChessMove> protectTheKing(ChessPosition startPosition) {
        TeamColor teamColor =  board.getPiece(startPosition).getTeamColor();
        return protectTheKing(teamColor, moveSet(teamColor, board, startPosition));
    }

    public Collection<ChessMove> protectTheKing(TeamColor teamColor, Collection<ChessMove> movesList){
        Collection<ChessMove> validMvs = new ArrayList<>();
        for (var attack : movesList){
            ChessBoard newBoard = board.copy();
            movePiece(newBoard, attack);
            if (!isInCheck(teamColor, newBoard)) {
                validMvs.add(attack);
            }
        }
        return validMvs;
    }





    public Collection<ChessMove> protectTheKing(TeamColor teamColor){
        // Change up -> isInCheck only gets called on the original board, and not on the board after the piece has been moved,
        // this causes issues for figuring out the valid moves especially with pawns
        // the solution here is to get a new list of allMoves for the board after the pieces get moved
        Collection<ChessMove> movesList = allMoves(teamColor, board);
        return protectTheKing(teamColor, movesList);
    }

    /**
     * create a function that collects the possible moves of all opposing pieces after a piece moves
     */






    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // same thing as inInCheckmate except the king shouldn't be in check
        // check first to see if king is even in check
        if (isInCheck(teamColor)){
            return false;
        }

        // look to see if there are any valid moves to get the king out of check
        Collection<ChessMove> movesList = allMoves(teamColor, board);
        movesList = protectTheKing(teamColor);
        return movesList.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "team=" + team +
                ", board=" + board +
                '}';
    }
}
