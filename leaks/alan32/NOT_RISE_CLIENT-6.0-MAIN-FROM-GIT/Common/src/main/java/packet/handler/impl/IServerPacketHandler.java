package packet.handler.impl;

import packet.handler.PacketHandler;
import packet.impl.server.community.ServerCommunityMessageSend;
import packet.impl.server.community.ServerCommunityPopulatePacket;
import packet.impl.server.general.ServerKeepAlive;
import packet.impl.server.login.ServerLoginPacket;
import packet.impl.server.protection.ServerConstantResult;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import packet.impl.server.protection.lIllIlIlIlIIIlllIIIlllIIIIlllI;
import packet.impl.server.store.IllIIIllllIlIlIIIllIlIllllIIllll;

public interface IServerPacketHandler extends PacketHandler {
    void handle(final ServerLoginPacket packet);

    void handle(final ServerCommunityPopulatePacket packet);

    void handle(final ServerCommunityMessageSend packet);

    void handle(final lIllIlIlIlIIIlllIIIlllIIIIlllI packet);

    void handle(final IllIIIllllIlIlIIIllIlIllllIIllll packet);

    void handle(final lIllIIlllIIIIlIllIIIIllIlllllIll packet);

    void handle(final ServerKeepAlive packet);

    void handle(ServerConstantResult serverConstantResult);
}
