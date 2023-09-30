/*
 * Decompiled with CFR 0.152.
 */
package packet.handler.impl;

import packet.handler.PacketHandler;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.client.community.ClientCommunityPopulateRequest;
import packet.impl.client.general.llIIlIlIllllIIllIllIIIIIIlIIIlII;
import packet.impl.client.login.lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl;
import packet.impl.client.protection.lIlIIlllllllIIlllI;
import packet.impl.client.protection.lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.client.store.lIIlIIIIIllIlllllllIIlllIlIllIlI;
import packet.impl.client.store.lllllIIlIIIlIIIIIIIlIlllIlIlIIlI;

public interface IClientPacketHandler
extends PacketHandler {
    public void handle(lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl var1);

    public void handle(lIlIIlllllllIIlllI var1);

    public void handle(ClientCommunityMessageSend var1);

    public void handle(lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI var1);

    public void handle(ClientCommunityPopulateRequest var1);

    public void handle(lllllIIlIIIlIIIIIIIlIlllIlIlIIlI var1);

    public void handle(lIIlIIIIIllIlllllllIIlllIlIllIlI var1);

    public void handle(lllIIllIlIIlIlllIllIlIIIIIlIlIIl var1);

    public void handle(llIIlIlIllllIIllIllIIIIIIlIIIlII var1);
}

