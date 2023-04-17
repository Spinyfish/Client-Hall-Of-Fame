package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import java.math.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInKeepAlive extends VPacket
{
    protected BigInteger id;
    private static final int count;
    
    @Override
    public int ordinal() {
        return VPacketPlayInKeepAlive.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public long getId() {
        return this.id.longValueExact();
    }
    
    static {
        count = VPacket.count();
    }
}
