package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInSetCreativeSlot extends VPacket
{
    private static final int count;
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInSetCreativeSlot.count;
    }
}
