package best.azura.irc.impl.packets.server;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class S6BanPacket extends Packet {

    public static int packetId = 0;

    public S6BanPacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public S6BanPacket(User user) {
        super(user);
        Id = packetId;
    }
}
