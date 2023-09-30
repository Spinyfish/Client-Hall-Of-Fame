package best.azura.irc.impl.packets.server;

import best.azura.irc.core.entities.Message;
import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import com.google.gson.JsonObject;

public class S1ChatSendPacket extends Packet {

    Message message;

    public static int packetId = 0;

    public S1ChatSendPacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public S1ChatSendPacket(User user, Message message) {
        super(user);
        Id = packetId;

        this.message = message;

        JsonObject jsonObject = new JsonObject();
        JsonObject jsonObject1 = new JsonObject();

        jsonObject1.addProperty("user", message.getUser().getUsername());
        jsonObject1.addProperty("content", message.getMessageContent());
        jsonObject1.addProperty("formatted", message.getFormattedMessage());
        jsonObject1.addProperty("channel", message.getChannelId());
        jsonObject1.addProperty("console", message.isConsole());

        jsonObject.add("message", jsonObject1);

        setContent(jsonObject);
    }

    public Message getMessage() {
        if (getContent().has("message") && getContent().getAsJsonObject("message").has("content") && getContent().getAsJsonObject("message").has("formatted") && getContent().getAsJsonObject("message").has("channel") && getContent().getAsJsonObject("message").has("console")) {
            message = new Message(null, getContent().getAsJsonObject("message").get("content").getAsString(), getContent().getAsJsonObject("message").get("channel").getAsInt(), getContent().getAsJsonObject("message").get("console").getAsBoolean());
            message.setFormattedContent(getContent().getAsJsonObject("message").get("formatted").getAsString());

            return message;
        }

        return null;
    }
}
