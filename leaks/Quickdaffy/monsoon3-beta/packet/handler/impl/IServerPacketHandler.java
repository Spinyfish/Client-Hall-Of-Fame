/*
 * Decompiled with CFR 0.152.
 */
package packet.handler.impl;

import packet.handler.PacketHandler;
import packet.impl.server.community.ServerCommunityMessageSend;
import packet.impl.server.community.ServerCommunityPopulatePacket;
import packet.impl.server.general.llIllllIIIlIIlIIllIlIIIllIIIIIIl;
import packet.impl.server.login.lIIlllIIIllIIIIIllIIIllllIllIllI;
import packet.impl.server.protection.lIlIIIllIlIIIlllIllI;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import packet.impl.server.protection.lIllIlIlIlIIIlllIIIlllIIIIlllI;
import packet.impl.server.store.IllIIIllllIlIlIIIllIlIllllIIllll;

public interface IServerPacketHandler
extends PacketHandler {
    public void handle(lIIlllIIIllIIIIIllIIIllllIllIllI var1);

    public void handle(ServerCommunityPopulatePacket var1);

    public void handle(ServerCommunityMessageSend var1);

    public void handle(lIllIlIlIlIIIlllIIIlllIIIIlllI var1);

    public void handle(IllIIIllllIlIlIIIllIlIllllIIllll var1);

    public void handle(lIllIIlllIIIIlIllIIIIllIlllllIll var1);

    public void handle(llIllllIIIlIIlIIllIlIIIllIIIIIIl var1);

    public void handle(lIlIIIllIlIIIlllIllI var1);
}

