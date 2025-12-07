package websocket.messages;


/*
Command
 - ERROR
Required Fields
 - String errorMessage
Description
 - This message is sent to a client when it sends an invalid command.
    The message must include the word Error.


 */
public class ErrorMessage extends ServerMessage{
    public String errorMessage;
    public ErrorMessage(String errorMsg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMsg;

    }
}
