package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.WebSocket.NotificationHandler;
import ui.WebSocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GameClient implements NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private final Repl repl;
    private final String playerColor; // "WHITE", "BLACK", or null for observer

    public GameClient(ServerFacade server, Repl repl, String playerColor, WebSocketFacade ws, ChessGame game) {
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
        var piecePos = params[0];

        if (server.getAuthToken() == null) {
            return SET_TEXT_COLOR_RED + "unable to authenticate user";

        }
    }

    private String resign() {
    }

    private String makeMove(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "please enter piece position as one word (example: show a1)";

        }
        var pieceMv = params[0];

        if (server.getAuthToken() == null) {
            return SET_TEXT_COLOR_RED + "unable to authenticate user";

        }

    }


    public String help() {
        return """
                [Options] : [what to type]
                - Go back to menu: "leave"
                - Redraw chess board: "redraw"
                - Help: "help"
                """;
    }

    private String leave() {
        return "Leaving game";
    }

    public String redraw() {
        BoardSetUp boardSetUp = new BoardSetUp();
        String teamSide = (playerColor != null && playerColor.equalsIgnoreCase("BLACK")) ? "black" : "white";
        return boardSetUp.redraw(teamSide);
    }


    @Override
    public void notify(ServerMessage notification) {

    }
}
