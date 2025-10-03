package socket;

import com.google.gson.Gson;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import entity.Chat;
import static org.hibernate.criterion.Restrictions.eq;

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
            try {
                WS.getBasicRemote().sendText(ChatService.GSON.toJson(payload));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<ChatSummary> getFriendChatsForUser(String userContact) {
        try {
            org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(Chat.class);
            Criterion rest1 = Restrictions.eq("fromUser", userContact),Restrictions.eq("toUser", userContact));
            c.add(rest1);
            c.addOrder(Order.desc(userContact).desc("updatedAt"));
            List<Chat> chats = c.list();
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Data fetch failed");
        }
    }
}
