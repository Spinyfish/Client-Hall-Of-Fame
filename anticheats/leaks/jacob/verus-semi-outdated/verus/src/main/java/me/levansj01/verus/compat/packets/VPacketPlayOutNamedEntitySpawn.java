package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.transaction.tracker.*;

public abstract class VPacketPlayOutNamedEntitySpawn extends VPacket
{
    protected double z;
    protected double x;
    private static final int count;
    protected byte pitch;
    protected byte yaw;
    protected double y;
    protected int id;
    
    public PacketLocation toLocation(final long n) {
        return new PacketLocation(this.x, this.y, this.z, n, Long.MAX_VALUE);
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutNamedEntitySpawn.count;
    }
    
    public byte getYaw() {
        return this.yaw;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public void setPitch(final byte pitch) {
        this.pitch = pitch;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public void setYaw(final byte yaw) {
        this.yaw = yaw;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public double getY() {
        return this.y;
    }
    
    static {
        count = VPacket.count();
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getX() {
        return this.x;
    }
    
    public int getId() {
        return this.id;
    }
    
    public byte getPitch() {
        return this.pitch;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public IServerLocation location() {
        return (IServerLocation)new BasicLocation(this.x, this.y, this.z);
    }
}
