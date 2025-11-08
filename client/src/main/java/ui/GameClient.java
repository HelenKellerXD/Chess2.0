package ui;

import java.util.Arrays;

public class GameClient {
    private final ServerFacade server;
    private final Repl repl;
    private final String playerColor; // "WHITE", "BLACK", or null for observer

    public GameClient(ServerFacade server, Repl repl, String playerColor) {
        this.server = server;
        this.repl = repl;
        this.playerColor = playerColor;
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
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }



    private String help() {
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

}
