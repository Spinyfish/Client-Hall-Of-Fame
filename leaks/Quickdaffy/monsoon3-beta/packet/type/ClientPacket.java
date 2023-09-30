/*
 * Decompiled with CFR 0.152.
 */
package packet.type;

import packet.Packet;
import packet.handler.impl.IClientPacketHandler;

public interface ClientPacket
extends Packet<IClientPacketHandler> {
    @Override
    public void process(IClientPacketHandler var1);
}

