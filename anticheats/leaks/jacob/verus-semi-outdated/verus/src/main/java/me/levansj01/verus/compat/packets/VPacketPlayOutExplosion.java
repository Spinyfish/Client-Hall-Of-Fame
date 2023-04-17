package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutExplosion extends VPacket
{
    protected float motX;
    private static final int count;
    protected float motY;
    protected float motZ;
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public float getMotZ() {
        return this.motZ;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutExplosion.count;
    }
    
    public float getMotX() {
        return this.motX;
    }
    
    public float getMotY() {
        return this.motY;
    }
}
