package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInTransaction extends VPacket implements Transactionable
{
    private static final int count;
    protected short id;
    
    static {
        count = VPacket.count();
    }
    
    public short getId() {
        return this.id;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handleIn(this);
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInTransaction.count;
    }
    
    @Override
    public short id() {
        return this.id;
    }
}
