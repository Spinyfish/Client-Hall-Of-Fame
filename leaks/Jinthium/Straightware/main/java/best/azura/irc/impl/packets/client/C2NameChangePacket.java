package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C2NameChangePacket extends Packet {

    public static int packetId = 0;

    public C2NameChangePacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C2NameChangePacket(User user) {
        super(user);
        Id = packetId;
    }

    public String getName() {
        if (!getContent().has("name"))
            return null;

        return getContent().get("name").getAsString();
    }
}
