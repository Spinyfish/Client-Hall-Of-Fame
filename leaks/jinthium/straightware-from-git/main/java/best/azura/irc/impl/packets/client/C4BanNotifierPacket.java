package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C4BanNotifierPacket extends Packet {

    public static int packetId = 0;

    public C4BanNotifierPacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C4BanNotifierPacket(User user) {
        super(user);
        Id = packetId;
    }

    public String getServer() {
        if (getContent() == null || !getContent().has("server"))
            return null;

        return getContent().get("server").getAsString();
    }

}
