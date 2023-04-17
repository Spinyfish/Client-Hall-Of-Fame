package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInCloseWindow extends VPacket
{
    private static final int count;
    protected int window;
    
    @Override
    public int ordinal() {
        return VPacketPlayInCloseWindow.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    static {
        count = VPacket.count();
    }
}
