package ui.websocketclient;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;


import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    String auth;
    Integer gameID;
    ChessGame chessGame;

    public WebSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                /* this is where the message from the server is received. at this point,
                the facde now needs to take the message and depending on what type it is
                 */
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void send (String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void makeMove(ChessMove move) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth,  gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception( ex.getMessage());
        }
    }

    public void resign() throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth,  gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception( ex.getMessage());
        }
    }

    public void highlightMoves(ChessPosition position) throws Exception {
        chessGame.allMoves(ChessGame.TeamColor.WHITE, chessGame.getBoard());
    }

    public void redraw() throws Exception {
        send("hello");
    }

}