package socket;

/**
 *
 * @author Laky
 */
public class ChatSummary {

    private String friendContact;
    private String friendName;
    private String lastMessage;
    private long lastTimeStamp;
    private int unreadCount;
    private String profileImage;

    public ChatSummary() {
    }

    public ChatSummary(String friendContact, String friendName, String lastMessage, long lastTimeStamp, int unreadCount, String profileImage) {
        this.friendContact = friendContact;
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.unreadCount = unreadCount;
        this.profileImage = profileImage;
    }

    public String getFriendContact() {
        return friendContact;
    }

    public void setFriendContact(String friendContact) {
        this.friendContact = friendContact;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
