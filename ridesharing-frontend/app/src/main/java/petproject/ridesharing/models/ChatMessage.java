package petproject.ridesharing.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class ChatMessage implements Serializable {
    private String id, message, createdAt;
    private User user;

    public ChatMessage() {
    }

    public ChatMessage(String id, String message, String createdAt, User user) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
    }

    public static ChatMessage createMessageFromJSON(JSONObject jsonObject) {
        try {
            return new ChatMessage(
                    jsonObject.getString("id"),
                    jsonObject.getString("message_text"),
//                    jsonObject.getString("created_at"),
                    "",
                    new User(jsonObject.getInt("from_user") + "", "", "")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
