package best.azura.irc.impl.packets.server;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import com.google.gson.JsonArray;

public class S0LoginResponsePacket extends Packet {

    public static int packetId = 0;

    public S0LoginResponsePacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public S0LoginResponsePacket() {
        super((User)null);
        Id = packetId;
    }

    public JsonArray getUserList() {
        return getContent().get("users").getAsJsonArray();
    }

    public JsonArray getChannelList() { return getContent().get("channels").getAsJsonArray(); }

    public boolean isSuccess() {
        return getContent().has("success") && getContent().get("success").getAsBoolean();
    }
}
