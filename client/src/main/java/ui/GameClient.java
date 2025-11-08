package ui;

import java.util.Arrays;

public class GameClient {
    private final ServerFacade server;
    private final Repl repl;

    public GameClient(ServerFacade server, Repl repl, String playerColor) {
        this.server = server;
        this.repl = repl;

    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "leave" -> "leave";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }





    public String help() {
        return """
                [Options] : [what to type]
                - Go Back to menu: "leave"
                - Help: "help"
                
                """;

    }

}
