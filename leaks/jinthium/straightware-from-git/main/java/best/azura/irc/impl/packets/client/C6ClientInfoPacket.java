package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C6ClientInfoPacket extends Packet {

    public static int packetId = 0;

    public C6ClientInfoPacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C6ClientInfoPacket(User user) {
        super(user);
        Id = packetId;
    }
}
