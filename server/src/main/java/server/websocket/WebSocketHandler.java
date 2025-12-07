package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    ///  this function is what handles and delegates the game commands that are passed in from the websocket that are commands
    public void handleMessage(WsMessageContext ctx) throws Exception{
        Session session = ctx.session;
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            int gameID = command.getGameID();
            connections.add(gameID, ctx.session);

            switch (command.getCommandType()) {
                ///  this is where you put in the commands (CONNECT, MAKE_MOVE, LEAVE, RESIGN)
                case CONNECT -> connect(ctx, command);
                case MAKE_MOVE -> {
                    /// command need to be re-deserialized as a MakeMoveCommand since it has the MovePiece field as well
                    MakeMoveCommand cmd = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    mkMv(ctx , cmd);
                }
                case LEAVE -> leave(ctx, command);
                case RESIGN -> resign(ctx, command);
            }
        } catch (Exception ex) {
            ServerMessage e = new ErrorMessage(ex.getMessage());
            ctx.send(e.toString());
            ex.printStackTrace();
        }
        ctx.send("WebSocket response: " + ctx.message());
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        connections.remove(ctx.session);
    }


    private void redraw(Session session, int gameID) throws Exception {
        ///  figure this part out ----------------------- where do I pull the game from?????????
        GameData data = gameService.getGame(gameID);
        ChessGame game = data.game();

        LoadGameMessage msg = new LoadGameMessage(game);
        connections.broadcastToSession(session, msg);
    }

    // Used for a user to make a WebSocket connection as a player or observer.
    private void connect(WsMessageContext ctx, UserGameCommand cmd) throws Exception {
        String userName = userService.getUsername(cmd.getAuthToken());

        connections.add(cmd.getGameID(), ctx.session);
        redraw(ctx.session, cmd.getGameID());

        ServerMessage notification = new NotificationMessage(
                userName + " connected to the game");
        connections.broadcastToGame(cmd.getGameID(), ctx.session, notification);
    }

    //Used to request to make a move in a game. (required additional fields
    private void mkMv(WsMessageContext ctx, MakeMoveCommand mv) throws Exception {
        String usrName = userService.getUsername(mv.getAuthToken());
        GameData nwGameData = gameService.makeMove(mv.getGameID(), mv.mv, usrName);

        // broadcast new board
        LoadGameMessage loadGameMessage = new LoadGameMessage(nwGameData.game());
        connections.broadcastToGame(mv.getGameID(), null, loadGameMessage);

        // broadcast what move was made
        String message = usrName + "moved " + mv.mv.getStartPosition()
                + " to " + mv.mv.getEndPosition() + ".";
        ServerMessage notification = new NotificationMessage(message);
        connections.broadcastToGame(mv.getGameID(), ctx.session, notification);
    }

    private void leave(WsMessageContext ctx, UserGameCommand cmd) throws Exception {
        ServerMessage notification = new NotificationMessage(
                cmd.getGameID().toString() + " left the game");
        connections.broadcastToGame(cmd.getGameID(), ctx.session, notification);
        connections.remove(ctx.session);
    }
    private void resign(WsMessageContext ctx, UserGameCommand cmd) throws Exception {
        ServerMessage notification = new NotificationMessage(cmd.getGameID().toString() + " resigned from the game");
        connections.broadcastToGame(cmd.getGameID(), ctx.session, notification);
        connections.remove(ctx.session);

    }

}
