package best.azura.irc.core.packet;

import best.azura.irc.core.entities.User;
import com.google.gson.JsonObject;

public class Packet {

    protected int Id;

    User user;

    JsonObject content;

    boolean encrypt = true;

    public Packet(int Id) {
        this.Id = Id;
    }

    public Packet(User user) {
        this.user = user;
    }

    public Packet(JsonObject content) {
        this.content = content;
    }

    public int getId() {
        return Id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setContent(JsonObject jsonObject) {
        content = jsonObject;
    }

    public JsonObject getContent() {
        return content != null ? content : new JsonObject();
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
}
