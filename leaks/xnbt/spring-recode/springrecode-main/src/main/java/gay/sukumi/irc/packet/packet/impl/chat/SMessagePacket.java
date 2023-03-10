package gay.sukumi.irc.packet.packet.impl.chat;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import gay.sukumi.irc.profile.UserProfile;
import gay.sukumi.irc.utils.Crypter;
import io.netty.buffer.ByteBuf;

@PacketId(6)
public class SMessagePacket extends Packet {

    private UserProfile userProfile;
    private String message;
    private Type type;

    @Override
    public void read(ByteBuf byteBuf) {
        message = Crypter.decode(readString(byteBuf));
        type = Type.valueOf(readString(byteBuf));
        if (type != Type.RAW)
            userProfile = readUserProfile(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        RAW, USER
    }
}
