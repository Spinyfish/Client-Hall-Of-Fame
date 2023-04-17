package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutSpawnEntityLiving extends VPacket
{
    private static final int count;
    protected int id;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutSpawnEntityLiving.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    static {
        count = VPacket.count();
    }
    
    public int getId() {
        return this.id;
    }
}
