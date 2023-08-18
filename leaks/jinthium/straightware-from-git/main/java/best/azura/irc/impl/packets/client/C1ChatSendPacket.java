package best.azura.irc.impl.packets.client;

import best.azura.irc.core.entities.Message;
import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;

public class C1ChatSendPacket extends Packet {

    Message message;

    public static int packetId = 0;

    public C1ChatSendPacket(int Id) {
        super(Id);
        packetId = Id;
    }

    public C1ChatSendPacket(User user) {
        super(user);
        Id = packetId;
    }

    public Message getMessage() {
        if (!getContent().has("message"))
            return null;

        int channelId = getContent().has("channel") ? getContent().get("channel").getAsInt() : 0;

        if (message == null)
            message = new Message(getUser(), getContent().get("message").getAsString(), channelId, false);

        return message;
    }
}
