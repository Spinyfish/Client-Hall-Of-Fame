/*
 * Decompiled with CFR 0.152.
 */
package packet.type;

import packet.Packet;
import packet.handler.impl.IServerPacketHandler;

public interface ServerPacket
extends Packet<IServerPacketHandler> {
    @Override
    public void process(IServerPacketHandler var1);
}

