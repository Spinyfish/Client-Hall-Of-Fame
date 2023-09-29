package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C3KeepAlivePacket extends Packet {

    public static int packetId = 0;

    public C3KeepAlivePacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C3KeepAlivePacket() {
        super((User)null);
        Id = packetId;
    }
}
