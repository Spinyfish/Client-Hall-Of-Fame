package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInSteerVehicle extends VPacket
{
    protected double strafe;
    protected double forward;
    private static final int count;
    
    public double getStrafe() {
        return this.strafe;
    }
    
    static {
        count = VPacket.count();
    }
    
    public double getForward() {
        return this.forward;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInSteerVehicle.count;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
