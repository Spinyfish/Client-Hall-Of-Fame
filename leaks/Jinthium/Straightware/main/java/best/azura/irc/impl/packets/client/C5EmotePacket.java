package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C5EmotePacket extends Packet {

    public static int packetId = 0;

    public C5EmotePacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C5EmotePacket(User user) {
        super(user);
        Id = packetId;
    }

    public String getEmote() {
        if (getContent() == null || !getContent().has("emote"))
            return null;

        return getContent().get("emote").getAsString();
    }

    public String getServer() {
        if (getContent() == null || !getContent().has("server"))
            return null;

        return getContent().get("server").getAsString();
    }
}
