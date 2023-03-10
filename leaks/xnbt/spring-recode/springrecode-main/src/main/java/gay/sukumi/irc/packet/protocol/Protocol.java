package gay.sukumi.irc.packet.protocol;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import gay.sukumi.irc.packet.packet.PacketHandler;
import gay.sukumi.irc.packet.packet.impl.chat.CMessagePacket;
import gay.sukumi.irc.packet.packet.impl.chat.SMessagePacket;
import gay.sukumi.irc.packet.packet.impl.login.LoginErrorPacket;
import gay.sukumi.irc.packet.packet.impl.login.LoginRequestPacket;
import gay.sukumi.irc.packet.packet.impl.login.LoginSuccessPacket;
import gay.sukumi.irc.packet.packet.impl.profile.CProfilePacket;
import gay.sukumi.irc.packet.packet.impl.profile.SProfilePacket;

/**
 * The chat client protocol
 * @author kittyuwu
 */
public class Protocol extends HydraProtocol {

    /*
     * Add packets & packet listeners
     */
    public Protocol() {

        registerPacket(LoginRequestPacket.class);
        registerPacket(LoginSuccessPacket.class);
        registerPacket(LoginErrorPacket.class);

        registerPacket(CMessagePacket.class);
        registerPacket(SMessagePacket.class);

        registerPacket(CProfilePacket.class);
        registerPacket(SProfilePacket.class);

        registerListener(new PacketHandler());
    }

}
