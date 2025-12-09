package ui.websocketclient;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);

    ;
}
