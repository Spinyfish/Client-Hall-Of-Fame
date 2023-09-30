package best.azura.irc.impl.packets.server;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import com.google.gson.JsonObject;

public class S7HandshakeResponse extends Packet {
    public static int packetId = 0;

    public S7HandshakeResponse(int Id) {
        super(Id);
        packetId = Id;
        setEncrypt(false);
    }

    public S7HandshakeResponse(String aesKey, boolean success, String reason) {
        super((User) null);
        Id = packetId;
        setEncrypt(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", aesKey);
        jsonObject.addProperty("success", success);
        jsonObject.addProperty("reason", reason);

        setContent(jsonObject);

    }

    public boolean isSuccess() {
        return getContent().has("success") && getContent().get("success").getAsBoolean();
    }

    public String getAESKey() {
        return getContent().has("key") ? getContent().get("key").getAsString() : null;
    }
}
