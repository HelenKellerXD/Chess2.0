package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.Javalin;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import java.util.Map;
import java.util.Objects;

// add javalin stuff
import io.javalin.http.Context;

public class Server {
    private final Javalin javalin;



    private UserService userService = new UserService();
    private GameService gameService = new GameService();

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        

        // Register your endpoints and handle exceptions here.

        // Register|  /user POST
        .post("/user", this::registerHandler)
        // Login| /session POST
        .post("/session", this::loginHandler)
        // Logout| /session DELETE
        .delete("/session", this::logoutHandler)
        // List Games| /game GET
        .get("/game", this::listGamesHandler)
        // Create Game| /game POST
        .post("/game", this::createGameHandler)
        // Join Game| /game PUT
        .put("/game", this::joinGameHandler)
        // Clear| /db DELETE
        .delete("/db", this::clearHandler);

    }


    private Object generalError (DataAccessException e, Context ctx){
        Gson gson = new Gson();
        if (e.getCause() instanceof java.sql.SQLException){
            ctx.status(500);
            return gson.toJson(Map.of("message", "Error: server error"));
        }
        // if not server error, then throw 401 error
        ctx.status(401);
        return gson.toJson(Map.of("message", "Error: unauthorized"));
    }

    private Object joinGameHandler(Context ctx) {
        // create Gson object and collect authToken from the request header
        Gson gson = new Gson();
        String authToken= ctx.header("authorization");
        String username;

        //validate auth token
        try {
            username = userService.getUsername(authToken);
        } catch (DataAccessException e) {
            return ctx.json(generalError(e, ctx));
        }


        // collect gameID from the request body
        JoinGameRequest userInfo = (gson.fromJson(ctx.body(), JoinGameRequest.class));
        JoinGameRequest joinGameRequest = new JoinGameRequest(userInfo.playerColor(), userInfo.gameID(), username);
        try {
            gameService.joinGame(joinGameRequest);
            ctx.status(200);
            return ctx.json(gson.toJson(null));
        } catch (DataAccessException e) {
            if (e.getCause() instanceof java.sql.SQLException){
                ctx.status(500);
                return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
            }
            // if e = bad join request -> 400, if e = game doesn't exist -> 403
            String error = e.getMessage();
            if (error.equalsIgnoreCase("Error: already taken")){
                ctx.status(403);
                return ctx.json(gson.toJson(Map.of("message", error)));

            }
            else {
                ctx.status(400);
                return ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
            }
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }
    }

    private Object createGameHandler(Context ctx) {
        // create Gson object and collect authToken from the request header
        Gson gson = new Gson();
        String authToken= ctx.header("authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        //validate auth token
        try {
            userService.validateToken(authToken);
        } catch (DataAccessException e) {
            return ctx.json(generalError(e, ctx));
        }


        // collect gameName from the request body
        CreateGameRequest userInfo = gson.fromJson(ctx.body(), CreateGameRequest.class);

        //check to see if body contains game name
        if(userInfo.gameName() == null){
            ctx.status(400);
            return ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }

        try {
            CreateGameResult result = gameService.createGame(userInfo);
            ctx.status(200);
            return ctx.json(gson.toJson(result));

        } catch (DataAccessException e) {
            return ctx.json(generalError(e, ctx));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }

    }

    private Object listGamesHandler(Context ctx) {
        // create Gson object and collect authToken from the request header
        Gson gson = new Gson();
        String authToken= ctx.header("authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        //validate auth token
        try {
            userService.validateToken(authToken);
        } catch (DataAccessException e) {
            return ctx.json(generalError(e, ctx));
        }


        try{
            //list games, return ListGamesResult result, and return code 200
            ListGamesResult result = gameService.listGames();
            ctx.status(200);
            return  ctx.json(gson.toJson(result));

        } catch(DataAccessException e){
            return ctx.json(generalError(e, ctx));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }

    }

    private Object logoutHandler(Context ctx) {
        Gson gson = new Gson();
        String authToken= ctx.header("authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        try{
            //logout, and return code 200
            userService.logout(logoutRequest);
            ctx.status(200);
            return  ctx.json(gson.toJson(null));

        } catch(DataAccessException e){
            return ctx.json(generalError(e, ctx));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }
    }

    private Object loginHandler(Context ctx) {
        Gson gson = new Gson();
        LoginRequest userInfo = gson.fromJson(ctx.body(), LoginRequest.class);
        LoginResult executionResult;

        if(userInfo.username() == null || userInfo.password() == null){
            ctx.status(400);
            return ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }

        try{
            //login user, create authToken, and return code 200
            executionResult = userService.login(userInfo);

            ctx.status(200);
            return  ctx.json(gson.toJson(executionResult));


        } catch(DataAccessException e){
            ctx.status(401);
            return ctx.json(generalError(e, ctx));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }
    }

    private Object clearHandler(Context ctx) {
        Gson gson = new Gson();
        try {
            userService.clear();
            gameService.clear();

        } catch (DataAccessException e) {
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }
        ctx.status(200);
        return  ctx.json(gson.toJson(null));
    }


    private Object registerHandler(Context ctx) {
        Gson gson = new Gson();
        RegisterRequest userInfo = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult executionResult;
        // check to see if all fields were properly entered
        if(userInfo.username() == null || userInfo.password() == null || userInfo.email() == null){
            ctx.status(400);
            return ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }


        try{
            //register user, create authToken, and return code 200
            executionResult = userService.register(userInfo);

            ctx.status(200);
            return  ctx.json(gson.toJson(executionResult));


        } catch(DataAccessException e){
            if (e.getCause() instanceof java.sql.SQLException){
                ctx.status(500);
                return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
            }
            // if register is not successful, then throw 403 error
            ctx.status(403);
            return ctx.json(gson.toJson(Map.of("message", "Error: already taken")));
        } catch (Exception e){
            ctx.status(500);
            return ctx.json(gson.toJson(Map.of("message", "Error: server error")));
        }
    }




    public void stop() {
        javalin.stop();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Server server = (Server) o;
        return Objects.equals(userService, server.userService) && Objects.equals(gameService, server.gameService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userService, gameService);
    }
}
