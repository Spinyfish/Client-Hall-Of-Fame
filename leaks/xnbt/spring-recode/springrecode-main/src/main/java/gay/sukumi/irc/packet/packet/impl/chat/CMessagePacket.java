package gay.sukumi.irc.packet.packet.impl.chat;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import gay.sukumi.irc.ChatClient;
import gay.sukumi.irc.utils.Crypter;
import io.netty.buffer.ByteBuf;

@PacketId(5)
public class CMessagePacket extends Packet {

    private String uuid;
    private String message;

    public CMessagePacket() {}

    public CMessagePacket(final String message) {
        this.message = message;
    }

    @Override
    public void read(ByteBuf byteBuf) {}

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, ChatClient.INSTANCE.getProfile().getUuid());
        writeString(byteBuf, Crypter.encode(message));
    }


    public String getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }
}
