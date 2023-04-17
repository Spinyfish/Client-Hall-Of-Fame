package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.data.transaction.tracker.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutEntityTeleport extends VPacket
{
    protected double x;
    protected int id;
    protected double z;
    protected byte yaw;
    protected byte pitch;
    protected double y;
    protected boolean ground;
    private static final int count;
    
    public int getId() {
        return this.id;
    }
    
    public IServerLocation location() {
        return (IServerLocation)new BasicLocation(this.x, this.y, this.z);
    }
    
    public double getZ() {
        return this.z;
    }
    
    public double getY() {
        return this.y;
    }
    
    static {
        count = VPacket.count();
    }
    
    public boolean isGround() {
        return this.ground;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutEntityTeleport.count;
    }
    
    public PacketLocation toLocation(final long n) {
        return new PacketLocation(this.x, this.y + 0.015625, this.z, n, Long.MAX_VALUE);
    }
    
    public byte getYaw() {
        return this.yaw;
    }
    
    public double getX() {
        return this.x;
    }
    
    public byte getPitch() {
        return this.pitch;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
