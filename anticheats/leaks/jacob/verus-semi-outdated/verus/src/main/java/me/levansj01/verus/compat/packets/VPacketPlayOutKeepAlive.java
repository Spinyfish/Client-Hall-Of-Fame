package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import java.math.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutKeepAlive extends VPacket
{
    protected BigInteger id;
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
        return VPacketPlayOutKeepAlive.count;
    }
    
    public long getId() {
        return this.id.longValueExact();
    }
}
