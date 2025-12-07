package websocket.messages;

/*
Command
 - NOTIFICATION
Required Fields
 - String message
Description
 - This is a message meant to inform a player when another player made an action.
 */
public class NotificationMessage extends ServerMessage{
    public String message;
    public NotificationMessage(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.message = msg;
    }

}
