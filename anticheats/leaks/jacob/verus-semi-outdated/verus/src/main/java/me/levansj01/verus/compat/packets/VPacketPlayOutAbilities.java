package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutAbilities extends VPacket
{
    private static final int count;
    protected boolean isInvulnerable;
    protected float flySpeed;
    protected boolean canInstantlyBuild;
    protected boolean isFlying;
    protected float walkSpeed;
    protected boolean canFly;
    
    @Override
    public int ordinal() {
        return VPacketPlayOutAbilities.count;
    }
    
    public boolean isFlying() {
        return this.isFlying;
    }
    
    static {
        count = VPacket.count();
    }
    
    public float getFlySpeed() {
        return this.flySpeed;
    }
    
    public float getWalkSpeed() {
        return this.walkSpeed;
    }
    
    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }
    
    public boolean isCanInstantlyBuild() {
        return this.canInstantlyBuild;
    }
    
    public boolean isCanFly() {
        return this.canFly;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
