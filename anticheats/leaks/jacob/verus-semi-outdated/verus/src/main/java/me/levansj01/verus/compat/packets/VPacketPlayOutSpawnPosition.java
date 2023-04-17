package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.wrapper.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutSpawnPosition extends VPacket
{
    protected BlockPosition position;
    private static final int count;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutSpawnPosition.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    public BlockPosition getPosition() {
        return this.position;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
