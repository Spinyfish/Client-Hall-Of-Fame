package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInCustomPayload extends VPacket
{
    protected String channel;
    protected int size;
    private static final int count;
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    static {
        count = VPacket.count();
    }
    
    public String getChannel() {
        return this.channel;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInCustomPayload.count;
    }
    
    public int getSize() {
        return this.size;
    }
}
