package ui;

import chess.*;
import ui.websocketclient.NotificationHandler;
import ui.websocketclient.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GameClient implements NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private final Repl repl;
    private final String playerColor; // "WHITE", "BLACK", or null for observer

    public GameClient(ServerFacade server, Repl repl, String playerColor, WebSocketFacade ws) {
        this.server = server;
        this.repl = repl;
        this.playerColor = playerColor;
        this.ws = ws;
        NotificationHandler notify;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "help" -> help();
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "show" -> showMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String showMoves(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "please enter piece position as one word (example: show a1)";

        }
        var piecePos = convertToPos(params[0]);

        if (server.getAuthToken() == null) {
            return SET_TEXT_COLOR_RED + "unable to authenticate user";

        }
        try {
            ws.highlightMoves(piecePos);
            return SET_TEXT_COLOR_BLUE + "game created\n";


        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Piece Not Found";
        }

    }

    private String resign() {
        try {
            ws.resign();
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "unable to resign";
        }
        return SET_TEXT_COLOR_BLUE + "game resigned\n";

    }

    private String makeMove(String[] params) {
        if (params.length != 2 || params.length != 3) {
            return SET_TEXT_COLOR_RED + "please enter the start position and end position as two words (example: move a2 a1)";

        }
        // extract the param and convert from alpha num to piece move type
        ChessPosition strtPos = convertToPos(params[0]);
        ChessPosition endPos = convertToPos(params[1]);
        ChessPiece.PieceType promo = null;
        if (params.length == 3){
            promo = convertToPromo(params[2]);
        }

        ChessMove mv = new ChessMove(strtPos, endPos, promo);

        if (server.getAuthToken() == null) {
            return SET_TEXT_COLOR_RED + "unable to authenticate user";

        }
        try {
            ws.makeMove(mv);
            return SET_TEXT_COLOR_BLUE + "Move made.\n";

        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "invalid move option" +
                    "If pawn has reached the end of the map, please also include what upgrade you would like for your pawn" +
                    "(example: a7 a8 queen) ";

        }

    }

    private ChessPiece.PieceType convertToPromo(String piece) {
        if (piece == null) {
            return null;
        }

        switch (piece.trim().toLowerCase()) {
            case "queen":
                return ChessPiece.PieceType.QUEEN;
            case "rook":
                return ChessPiece.PieceType.ROOK;
            case "bishop":
                return ChessPiece.PieceType.BISHOP;
            case "knight":
                return ChessPiece.PieceType.KNIGHT;
            default:
                return null;  // or throw an exception if invalid
        }
    }

    private ChessPosition convertToPos(String pos) {
        pos = pos.trim().toLowerCase(); // handle "A1" or "a1"

        int x = (pos.charAt(0) - 'a') + 1;
        int y = Character.getNumericValue(pos.charAt(1));

        return new ChessPosition(x,y);
    }



    public String help() {
        return """
                [Options] : [what to type]
                - Go back to menu: "leave"
                - Redraw chess board: "redraw"
                - Resign from game: "resign"
                - Help: "help"
         
                """;
    }

    private String leave() {
        return "Leaving game";
    }

    public String redraw() {
        BoardSetUp boardSetUp = new BoardSetUp();
        ChessBoard board = null;
        //litterally just added this for code quality
        try {
            ws.redraw();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String teamSide = (playerColor != null && playerColor.equalsIgnoreCase("BLACK")) ? "black" : "white";
        return boardSetUp.redraw(teamSide, board);
    }


    @Override
    public void notify(ServerMessage notification) {

    }
}
