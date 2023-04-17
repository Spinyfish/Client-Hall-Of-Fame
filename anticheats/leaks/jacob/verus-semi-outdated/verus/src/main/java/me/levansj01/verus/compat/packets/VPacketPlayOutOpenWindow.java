package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutOpenWindow extends VPacket
{
    private static final int count;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutOpenWindow.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    static {
        count = VPacket.count();
    }
}
