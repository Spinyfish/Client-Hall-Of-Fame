package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import java.util.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.*;
import me.levansj01.verus.util.location.*;
import me.levansj01.verus.data.transaction.teleport.*;

public abstract class VPacketPlayOutPosition extends VPacket implements ILocation
{
    protected Set flags;
    protected double z;
    protected double y;
    protected double x;
    protected float yaw;
    protected float pitch;
    private static final int count;
    
    public BasicTeleport toTeleport() {
        return new BasicTeleport(this.x, this.y, this.z, this.yaw, this.pitch, this.flags);
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public float getYaw() {
        return this.yaw;
    }
    
    @Override
    public float getPitch() {
        return this.pitch;
    }
    
    public Set getFlags() {
        return this.flags;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutPosition.count;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    public PlayerLocation toLocation(final PlayerData playerData) {
        return new PlayerLocation(System.currentTimeMillis(), playerData.getTotalTicks(), this.x, this.y, this.z, this.yaw, this.pitch, null, false);
    }
    
    static {
        count = VPacket.count();
    }
    
    public Teleport toTeleport(final int n, final long n2) {
        return new Teleport(this.x, this.y, this.z, this.yaw, this.pitch, this.flags, n, n2);
    }
    
    public enum TeleportFlag
    {
        Y_ROT("Y_ROT", 3), 
        Z("Z", 2);
        
        private static final TeleportFlag[] $VALUES;
        
        X_ROT("X_ROT", 4), 
        Y("Y", 1), 
        X("X", 0);
        
        private static TeleportFlag[] $values() {
            return new TeleportFlag[] { TeleportFlag.X, TeleportFlag.Y, TeleportFlag.Z, TeleportFlag.Y_ROT, TeleportFlag.X_ROT };
        }
        
        static {
            $VALUES = $values();
        }
        
        private TeleportFlag(final String s, final int n) {
        }
    }
}
