package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutSpawnEntity extends VPacket
{
    protected int id;
    private static final int count;
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutSpawnEntity.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getId() {
        return this.id;
    }
}
