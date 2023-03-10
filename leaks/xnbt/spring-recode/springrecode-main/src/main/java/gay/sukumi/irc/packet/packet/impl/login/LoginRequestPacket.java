package gay.sukumi.irc.packet.packet.impl.login;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import gay.sukumi.irc.profile.UserProfile;
import io.netty.buffer.ByteBuf;

@PacketId(0)
public class LoginRequestPacket extends Packet {

    private String username, password, mcUsername;
    private UserProfile.Client client;

    /* Empty constructor for Hydra, I don't know why it's like that. */
    public LoginRequestPacket() {}

    /**
     * @param username Username for authentication
     * @param password Password for authentication
     * @param client The client
     */
    public LoginRequestPacket(final String username, final String password, final String mcUsername, final UserProfile.Client client) {
        this.username = username;
        this.password = password;
        this.mcUsername = mcUsername;
        this.client = client;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, username);
        writeString(byteBuf, password);
        writeString(byteBuf, mcUsername);
        writeString(byteBuf, client.name());
    }

    @Override
    public void read(ByteBuf byteBuf) {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
