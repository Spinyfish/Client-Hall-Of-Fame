package gay.sukumi.irc.packet.packet;


import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.listener.Handler;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import gay.sukumi.irc.ChatClient;
import gay.sukumi.irc.packet.packet.impl.chat.SMessagePacket;
import gay.sukumi.irc.packet.packet.impl.login.LoginErrorPacket;
import gay.sukumi.irc.packet.packet.impl.login.LoginSuccessPacket;
import gay.sukumi.irc.packet.packet.impl.profile.SProfilePacket;

/**
 * I don't feel like commenting this too much.
 *
 * @author kittyuwu
 */
public class PacketHandler implements HydraPacketListener {

    /**
     * This function will be executed when the client receives a login success packet.
     *
     * @param packet  Login success packet itself
     * @param session Client session
     */
    @Handler
    public void onPacket(final LoginSuccessPacket packet, final Session session) {
        ChatClient.INSTANCE.setProfile(packet.getUserProfile());
        if (ChatClient.INSTANCE.getLoginListener() != null)
            ChatClient.INSTANCE.getLoginListener().onSuccess(packet.getUserProfile(), session);
    }

    /**
     * This function will be executed when the client receives a login error packet.
     *
     * @param packet  Login error packet itself
     * @param session Client session
     */
    @Handler
    public void onPacket(final LoginErrorPacket packet, final Session session) {
        if (ChatClient.INSTANCE.getLoginListener() != null)
            ChatClient.INSTANCE.getLoginListener().onFail(packet.getReason(), session);
    }

    /**
     * This function will be executed when a user updates their profile.
     *
     * @param packet  Profile update packet itself
     * @param session Client session
     */
    @Handler
    public void onPacket(final SProfilePacket packet, final Session session) {
        switch (packet.getAction()) {
            case ADD:
                ChatClient.INSTANCE.getConnectedUsers().add(packet.getProfile());
                if (packet.getProfile().getUsername().equalsIgnoreCase(ChatClient.INSTANCE.getProfile().getUsername())) {
                    return;
                }
                if (ChatClient.INSTANCE.getUserProfileListener() != null)
                    ChatClient.INSTANCE.getUserProfileListener().onConnected(packet.getProfile());
                break;
            case REMOVE:
                ChatClient.INSTANCE.getConnectedUsers().remove(ChatClient.INSTANCE.getUser(packet.getProfile().getUsername()));
                if (ChatClient.INSTANCE.getUserProfileListener() != null && !(packet.getProfile().getUsername().
                        equalsIgnoreCase(ChatClient.INSTANCE.getProfile().getUsername())))
                    ChatClient.INSTANCE.getUserProfileListener().onDisconnected(packet.getProfile());
                break;
            case CHANGE_NAME:
                ChatClient.INSTANCE.getUser(packet.getProfile().getUsername()).setMcUsername(packet.getProfile().getMcUsername());
                if (ChatClient.INSTANCE.getUserProfileListener() != null)
                    ChatClient.INSTANCE.getUserProfileListener().onNameChange(packet.getProfile());
                break;
            case CHANGE_SERVER:
                ChatClient.INSTANCE.getUser(packet.getProfile().getUsername()).setCurrentServer(packet.getProfile().getMcUsername());
                if (ChatClient.INSTANCE.getUserProfileListener() != null)
                    ChatClient.INSTANCE.getUserProfileListener().onServerChange(packet.getProfile());
                break;
        }
    }

    /**
     * This function will be executed when a user sends a message.
     *
     * @param packet  Chat message packet itself
     * @param session Client session
     */
    @Handler
    public void onPacket(final SMessagePacket packet, final Session session) {
        if (ChatClient.INSTANCE.getMessageListener() != null)
            ChatClient.INSTANCE.getMessageListener().onMessage(packet.getMessage(), packet.getType(), packet.getUserProfile());
    }


}
