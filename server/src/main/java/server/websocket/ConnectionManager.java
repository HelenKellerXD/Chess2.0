package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    ///  UPDATE THE CONNECTION MANAGER TO STORE THE SESSIONS BASED ON GAME ID AND NOT SESSION
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, g -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Session session) {
        for (Set<Session> gamesList : connections.values()){
            gamesList.remove(session);
        }
    }



    // figure out how to broadcast and be able to swp between broadcasting to a single user
    // (redraw) or to a whole group (player making move and then redrawing the board)
    public void sendToSession(Session session, ServerMessage msg) throws IOException {
        String json = gson.toJson(msg);
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(json);
        }

    }

    public void broadcastToGame(int gameID, Session excludeSession, ServerMessage msg) throws IOException {
        Set<Session> sessions = connections.get(gameID);
        if(sessions == null) {
            return;
        }
        String json = gson.toJson(msg);

        for (Session c : sessions) {
            if (c.isOpen() && !c.equals(excludeSession)) {
                c.getRemote().sendString(json);
            }
        }
    }
}