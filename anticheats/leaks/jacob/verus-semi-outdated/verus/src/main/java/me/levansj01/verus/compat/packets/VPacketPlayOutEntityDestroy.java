package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutEntityDestroy extends VPacket
{
    protected int[] ids;
    private static final int count;
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutEntityDestroy.count;
    }
    
    public int[] getIds() {
        return this.ids;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
