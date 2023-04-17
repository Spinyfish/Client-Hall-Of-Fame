package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VServerboundPongPacket extends VPacket implements Transactionable
{
    protected int id;
    private static final int count;
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handleIn(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    @Override
    public int ordinal() {
        return VServerboundPongPacket.count;
    }
    
    @Override
    public short id() {
        return (short)this.id;
    }
    
    static {
        count = VPacket.count();
    }
}
