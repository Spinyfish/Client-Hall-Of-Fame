package packet.handler.impl;

import packet.handler.PacketHandler;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.client.community.ClientCommunityPopulateRequest;
import packet.impl.client.data.ClientModuleData;
import packet.impl.client.general.ClientKeepAlive;
import packet.impl.client.login.ClientLoginPacket;
import packet.impl.client.protection.ClientConstantsPacket;
import packet.impl.client.protection.lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.client.store.lIIlIIIIIllIlllllllIIlllIlIllIlI;
import packet.impl.client.store.lllllIIlIIIlIIIIIIIlIlllIlIlIIlI;

public interface IClientPacketHandler extends PacketHandler {
    void handle(final ClientLoginPacket packet);
    void handle(final ClientConstantsPacket packet);
    void handle(final ClientCommunityMessageSend packet);
    void handle(final lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI packet);
    void handle(final ClientCommunityPopulateRequest packet);

    void handle(final lllllIIlIIIlIIIIIIIlIlllIlIlIIlI packet);

    void handle(final lIIlIIIIIllIlllllllIIlllIlIllIlI packet);

    void handle(final lllIIllIlIIlIlllIllIlIIIIIlIlIIl packet);

    void handle(final ClientKeepAlive packet);

    void handle(final ClientModuleData packet);

}
