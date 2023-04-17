package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInHeldItemSlot extends VPacket
{
    private static final int count;
    protected int slot;
    
    @Override
    public int ordinal() {
        return VPacketPlayInHeldItemSlot.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getSlot() {
        return this.slot;
    }
}
