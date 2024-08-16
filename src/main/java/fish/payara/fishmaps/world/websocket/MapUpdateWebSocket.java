package fish.payara.fishmaps.world.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ServerEndpoint(value = "/update/map", encoders = ChunkMessageEncoder.class)
public class MapUpdateWebSocket {
    private static final Set<Session> SESSIONS = new HashSet<>();

    @OnOpen
    public void onOpen (Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose (Session session) {
        SESSIONS.remove(session);
    }

    @OnMessage
    public void onMessage (Session session, String message) {

    }

    public static void broadcastChunkUpdate (List<ChunkMessage> messages) {
        SESSIONS.forEach(
            session -> {
                try {
                    session.getBasicRemote().sendObject(messages);
                }
                catch (Exception ignored) {

                }
            }
        );
    }
}
