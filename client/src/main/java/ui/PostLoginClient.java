package ui;

import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PostLoginClient {
    private final ServerFacade server;
    private final Repl repl;
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    private int desiredGameID;
    private String teamColor;



    /*** list all the possible PostLogin actions
     * - help
     * - logout
     * - create game
     * - List Games
     * - Play Game
     * - Observe Game
     */
    public PostLoginClient(ServerFacade server, Repl repl) {
        this.server = server;
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "help" -> help();
                case "logout" -> "logout";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    public String createGame(String... params){
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "please enter the field: (game name)";

        }


        var gameName = params[0];

        if (server.getAuthToken() == null) {
            return SET_TEXT_COLOR_RED + "unable to authenticate user";

        }

        try {
            CreateGameResult result = server.createGame(new CreateGameRequest(gameName, server.getAuthToken()));
            if (result != null && result.gameID() > 0){
                return SET_TEXT_COLOR_BLUE + "game created\n";
            }
            else{
                return SET_TEXT_COLOR_RED + "unable to create game";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return SET_TEXT_COLOR_RED + "auth token:" + server.getAuthToken() + "\nerror in creating game" + e.getMessage();
        }

    }

    public String listGames(){
        try {
            gameIDs.clear();
            ListGamesRequest request = new ListGamesRequest(server.getAuthToken());
            ListGamesResult games = server.listGames();
            String result;
            if (games.games().isEmpty()){
                result = SET_TEXT_COLOR_BLUE + "There are no games currently \n";
            }
            else {
                result = SET_TEXT_COLOR_BLUE + "Games List:";
                int fakeID = 1;
                for (var game : games.games()) {
                    result += "ID " + fakeID + ". " + game.gameName() + "\n - white player: ";
                    if (game.whiteUsername() == null){
                        result += "N/A";
                    }
                    else{
                        result += game.whiteUsername();

                    }
                    result += "\n - black player: ";
                    if (game.blackUsername() == null){
                        result += "N/A";
                    }
                    else{
                        result += game.blackUsername();

                    }
                    result += "\n";
                    // create the list to contain the actual game IDs for later
                    gameIDs.add(game.gameID());
                    // increment i
                    fakeID++;
                }
            }
            return result;



        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Unable to retreive games";
        }
    }

    public String playGame(String... params){
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "please enter all fields: (gameID, WHITE/BLACK)";

        }
        int idIndex;
        try {
            idIndex = Integer.parseInt(params[0]) - 1;
        } catch (Exception e){
            return SET_TEXT_COLOR_RED + "ID must be a valid number";
        }
        if (gameIDs.size() <= idIndex || idIndex < 0) {
            return SET_TEXT_COLOR_RED + "not an ID number";

        }

        teamColor = params[1];
        if (!teamColor.equalsIgnoreCase("WHITE") && !teamColor.equalsIgnoreCase("BLACK")){
            return SET_TEXT_COLOR_RED + "color has to be 'white' or 'black'";

        }

        JoinGameRequest request = new JoinGameRequest(teamColor, gameIDs.get(idIndex), server.getUsername());

        try {
            server.joinGame(request);
            return SET_TEXT_COLOR_BLUE + "joined game";
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "cannot join game as " + teamColor;
        }
    }

    public String observeGame(String... params){
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "please enter all fields: (gameID)";

        }
        int idIndex;
        try {
            idIndex = Integer.parseInt(params[0]) - 1;
        } catch (Exception e){
            return SET_TEXT_COLOR_RED + "ID must be a number";
        }
        if (gameIDs.size() <= idIndex || idIndex < 0) {
            return SET_TEXT_COLOR_RED + "not an ID number";

        }
        desiredGameID = gameIDs.get(idIndex);
        return SET_TEXT_COLOR_BLUE + "observing game";
        }

    public String help() {
        return """
                GAME MENU OPTIONS
                
                - Help: "help"
                - Logout: "logout"
                - Create Game: "create <game name>"
                - List Games: "list"
                - Play Game: "play <game ID> <WHITE/BLACK>"
                - Observe Game: "observe <game ID>"
                """;
    }

    public int getGameID() {
        return desiredGameID;
    }

    public String getTeamColor(){
        return teamColor;
    }

}




