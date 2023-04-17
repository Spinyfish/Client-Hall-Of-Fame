package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutGameStateChange extends VPacket
{
    protected int setting;
    private static final int count;
    protected float value;
    
    public int getSetting() {
        return this.setting;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutGameStateChange.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public float getValue() {
        return this.value;
    }
}
