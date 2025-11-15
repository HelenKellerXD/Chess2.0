package ui;

import static ui.EscapeSequences.*;

public class BoardSetUp {


    public String redraw(String color) {
        String light = SET_BG_COLOR_LIGHT_GREY + EMPTY;
        String dark = SET_BG_COLOR_DARK_GREY + EMPTY;
        String d = SET_BG_COLOR_DARK_GREY;
        String l = SET_BG_COLOR_LIGHT_GREY;

        String[][] board = {
                {l + BLACK_ROOK, d + BLACK_KNIGHT, l + BLACK_BISHOP, d + BLACK_QUEEN,
                        l + BLACK_KING, d + BLACK_BISHOP, l + BLACK_KNIGHT, d + BLACK_ROOK},
                {d + BLACK_PAWN, l + BLACK_PAWN, d + BLACK_PAWN, l + BLACK_PAWN,
                        d + BLACK_PAWN, l + BLACK_PAWN, d + BLACK_PAWN, l + BLACK_PAWN},
                {light, dark, light, dark, light, dark, light, dark},
                {dark, light, dark, light, dark, light, dark, light},
                {light, dark, light, dark, light, dark, light, dark},
                {dark, light, dark, light, dark, light, dark, light},
                {l + WHITE_PAWN, d + WHITE_PAWN, l + WHITE_PAWN, d + WHITE_PAWN, l + WHITE_PAWN,
                        d + WHITE_PAWN, l + WHITE_PAWN, d+ WHITE_PAWN},
                {d + WHITE_ROOK, l +WHITE_KNIGHT, d + WHITE_BISHOP, l + WHITE_QUEEN, d + WHITE_KING,
                        l + WHITE_BISHOP, d + WHITE_KNIGHT, l + WHITE_BISHOP},
        };

        if (color.equalsIgnoreCase("black")) {
            blackBoard(board);
        }

        return strBoard(board, color);
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
