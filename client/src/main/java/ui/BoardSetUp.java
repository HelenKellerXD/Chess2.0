package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;

public class BoardSetUp {
    String[][] showBoard;
    String drk = SET_BG_COLOR_DARK_GREY;
    String lht = SET_BG_COLOR_LIGHT_GREY;


    public String redraw(String teamColor, ChessBoard board) {


        fancyBoard(board);




        if (teamColor.equalsIgnoreCase("black")) {
            blackBoard(showBoard);
        }

        return strBoard(showBoard, teamColor);
    }

    void fancyBoard(ChessBoard board){
        for (int row = 8 ; row >= 1; row-- ){
            for (int col = 1 ; col <= 8; col++ ){
                ChessPiece q = board.getPiece(row,col);
                showBoard[row][col] = fancyTile(row,col) + fancyPiece(q);

            }
            System.out.println("|");

        }
    }

    /// gets floor of spot position to tile the board
    private String fancyTile(int row, int col){
        if ((row+col)%2 == 0){
            return drk;
        }
        else{
            return lht;
        }
    }


    private String fancyPiece(ChessPiece chessPiece){
        if (chessPiece == null){
            return EMPTY;
        }
        else{
            if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                switch (chessPiece.getPieceType()){
                    case KING -> {
                        return WHITE_KING;
                    }
                    case QUEEN -> {
                        return WHITE_QUEEN;
                    }
                    case ROOK -> {
                        return WHITE_ROOK;
                    }
                    case BISHOP -> {
                        return WHITE_BISHOP;
                    }
                    case KNIGHT -> {
                        return WHITE_KNIGHT;
                    }
                    case PAWN -> {
                        return WHITE_PAWN;
                    }
                };
            }
            else{
                switch (chessPiece.getPieceType()){
                    case KING -> {
                        return BLACK_KING;
                    }
                    case QUEEN -> {
                        return BLACK_QUEEN;
                    }
                    case ROOK -> {
                        return BLACK_ROOK;
                    }
                    case BISHOP -> {
                        return BLACK_BISHOP;
                    }
                    case KNIGHT -> {
                        return BLACK_KNIGHT;
                    }
                    case PAWN -> {
                        return BLACK_PAWN;
                    }
                };
            }

        }
        return null;
    }

    private void blackBoard(String[][] board) {
        for (int i = 0; i < board.length / 2; i++) {
            String[] temp = board[i];
            board[i] = board[board.length - 1 - i];
            board[board.length - 1 - i] = temp;
        }
        for (String[] row : board) {
            for (int j = 0; j < row.length / 2; j++) {
                String temp = row[j];
                row[j] = row[row.length - 1 - j];
                row[row.length - 1 - j] = temp;
            }
        }
    }

    private String strBoard(String[][] board, String color) {
        StringBuilder strBoard = new StringBuilder();
        String[] files = {"  a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};

        strBoard.append("\n   ");
        if (color.equalsIgnoreCase("white")) {
            for (String f : files) {
                strBoard.append(f).append("  ");}
        } else {
            for (int i = files.length - 1; i >= 0; i--) {
                strBoard.append(files[i]).append("  ");
            }
        }
        strBoard.append("\n   ----------------------------------------\n");

        for (int i = 0; i < 8; i++) {
            int team = (color.equalsIgnoreCase("white")) ? (8 - i) : (i + 1);
            strBoard.append(team).append(" |");
            for (int j = 0; j < 8; j++) {
                strBoard.append(" ").append(board[i][j]).append(" ");
            }
            strBoard.append("| ").append(team).append("\n");
        }

        strBoard.append("   ----------------------------------------\n   ");
        if (color.equalsIgnoreCase("white")) {
            for (String f : files) {
                strBoard.append(f).append("  ");
            }
        } else {
            for (int i = files.length - 1; i >= 0; i--) {
                strBoard.append(files[i]).append("  ");
            }
        }
        strBoard.append("\n");

        return strBoard.toString();
    }
}
