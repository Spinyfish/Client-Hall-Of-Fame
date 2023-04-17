package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.util.location.*;

public abstract class VPacketPlayInFlying extends VPacket implements IPacketLocation
{
    protected boolean look;
    protected double z;
    protected boolean pos;
    protected boolean ground;
    protected float yaw;
    protected double y;
    private static final int count;
    protected double x;
    protected float pitch;
    protected boolean teleport;
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public boolean isGround() {
        return this.ground;
    }
    
    public void setTeleport(final boolean teleport) {
        this.teleport = teleport;
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public float getYaw() {
        return this.yaw;
    }
    
    public boolean isTeleport() {
        return this.teleport;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInFlying.count;
    }
    
    @Override
    public boolean isLook() {
        return this.look;
    }
    
    public PacketLocation from(final PacketLocation packetLocation) {
        double n;
        double n2;
        double n3;
        if (this.pos) {
            n = this.x;
            n2 = this.y;
            n3 = this.z;
        }
        else {
            n = packetLocation.getX();
            n2 = packetLocation.getY();
            n3 = packetLocation.getZ();
        }
        float n4;
        float n5;
        if (this.look) {
            n4 = this.yaw;
            n5 = this.pitch;
        }
        else {
            n4 = packetLocation.getYaw();
            n5 = packetLocation.getPitch();
        }
        return new PacketLocation(n, n2, n3, n4, n5, this.ground, this.pos, this.look, this.teleport);
    }
    
    @Override
    public float getPitch() {
        return this.pitch;
    }
    
    @Override
    public boolean isPos() {
        return this.pos;
    }
}
