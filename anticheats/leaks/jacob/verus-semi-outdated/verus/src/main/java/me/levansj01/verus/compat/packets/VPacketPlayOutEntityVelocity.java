package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutEntityVelocity extends VPacket
{
    private static final int count;
    protected int motX;
    protected int id;
    protected int motZ;
    protected int motY;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutEntityVelocity.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getMotY() {
        return this.motY;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getMotX() {
        return this.motX;
    }
    
    public int getMotZ() {
        return this.motZ;
    }
}
