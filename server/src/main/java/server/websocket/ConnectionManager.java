package server.websocket;


import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    ///  UPDATE THE CONNECTION MANAGER TO STORE THE SESSIONS BASED ON GAME ID AND NOT SESSION
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, g -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Session session) {
        for (Set<Session> gamesList : connections.values()){
            gamesList.remove(session);
        }
    }


    // figure out how to broadcast and be able to swp between broadcasting to a single user (redraw) or to a whole group (player making move and then redrawing the board)
    public void broadcastToSession(Session session, ServerMessage srvMsg) throws IOException {
        String msg = srvMsg.toString();
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(msg.toString());
        }

    }

    public void broadcastToGame(int gameID, Session excludeSession, ServerMessage srvMsg) throws IOException {
        String msg = srvMsg.toString();
        Set<Session> sessions = connections.get(gameID);
        if(sessions == null) {
            return;
        }

        String json = msg.toString();

        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}