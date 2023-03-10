package gay.sukumi.irc.packet.packet.impl.login;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import gay.sukumi.irc.profile.UserProfile;
import io.netty.buffer.ByteBuf;

@PacketId(1)
public class LoginSuccessPacket extends Packet {

    private UserProfile userProfile;

    @Override
    public void read(ByteBuf byteBuf) {
        userProfile = readUserProfile(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

}
