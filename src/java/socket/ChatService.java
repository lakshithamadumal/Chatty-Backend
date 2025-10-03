package socket;

import com.google.gson.Gson;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

/**
 *
 * @author Laky
 */
public class ChatService {
    
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    
    public static void register(String userId, Session session) {
        ChatService.SESSIONS.put(userId, session);
    }
    
    public static void unregister(String userId) {
        ChatService.SESSIONS.remove(userId);
    }
    
    public static void sendToUser(String userId, Object payload) {
        Session WS = ChatService.SESSIONS.get(userId);
        if (WS != null && WS.isOpen()) {
            
        }
    }
}
