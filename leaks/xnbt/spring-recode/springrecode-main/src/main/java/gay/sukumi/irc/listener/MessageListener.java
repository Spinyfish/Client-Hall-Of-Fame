package gay.sukumi.irc.listener;

import gay.sukumi.irc.packet.packet.impl.chat.SMessagePacket;
import gay.sukumi.irc.profile.UserProfile;

public interface MessageListener {

    void onMessage(String message, SMessagePacket.Type type, UserProfile userProfile);

}
