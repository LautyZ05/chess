package server.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public record participant(int gameID, String username) {}

    private final Map<Session, participant> participants = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, String username){
        participants.put(session, new participant(gameID, username));
    }

    public participant remove(Session session){
        return participants.remove(session);
    }

    public participant get(Session session){
        return participants.get(session);
    }

    public void send(Session session, ServerMessage message){
        if (!trySend(session, new Gson().toJson(message))){
            participants.remove(session);
        }
    }

    public void broadcastToOthers(int gameID, Session exclude, ServerMessage message){
        for (Map.Entry<Session, participant> entry : participants.entrySet()){
            Session session = entry.getKey();
            if (entry.getValue().gameID() != gameID || session.equals(exclude)){
                continue;
            }
            if (!trySend(session, new Gson().toJson(message))){
                participants.remove(session);
            }
        }
    }

    private boolean trySend(Session session, String json){
        try{
            if (!session.isOpen()){
                return false;
            }
            session.getRemote().sendString(json);
            return true;
        }
        catch (IOException e){
            return false;
        }
    }
}
