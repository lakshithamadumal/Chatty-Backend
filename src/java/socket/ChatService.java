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
import entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.Transaction;
import static org.hibernate.criterion.Restrictions.eq;

/**
 *
 * @author Laky
 */
public class ChatService {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private static final String URL = "http://localhost:8080"; //ngrokproxy url

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
            Criteria c1 = session.createCriteria(Chat.class);
            Criterion rest1 = Restrictions.or(Restrictions.eq("from", userContact),
                    Restrictions.eq("to", userContact));

            c1.add(rest1);
            c1.addOrder(Order.desc("updatedAt"));

            List<Chat> chats = c1.list();
            Map<String, ChatSummary> map = new LinkedHashMap<>();

            for (Chat chat : chats) {
                String friendContact = chat.getFrom().getContactNo().equals(userContact)
                        ? chat.getTo().getContactNo() : chat.getFrom().getContactNo();
                if (!map.containsKey(friendContact)) {
                    Criteria c2 = session.createCriteria(User.class);
                    c2.add(Restrictions.eq("contactNo", friendContact));
                    User friend = (User) c2.uniqueResult();

                    String profileImage = ChatService.URL + "/Chatty/profile-images/" + friend.getId() + "/profile.png";
                    int unread = 2;
                    map.put(friendContact, new ChatSummary(
                            friendContact,
                            friend.getFirstName() + " " + friend.getLastName(),
                            chat.getMessage(),
                            chat.getUpdatedAt(),
                            unread,
                            profileImage));
                }
            }

            return new ArrayList<>(map.values());
        } catch (Exception e) {
            throw new RuntimeException("Data fetch failed!");
        }
    }

    public static void deliverChat(Chat chat) {
        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();
        session.persist(chat);
        tr.commit();

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "chat");
        envelope.put("payload", chat);

        ChatService.sendToUser(chat.getTo().getContactNo(), envelope);
        ChatService.sendToUser(chat.getFrom().getContactNo(), envelope);
    }

}
