package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutTransaction extends VPacket implements Transactionable
{
    protected boolean accepted;
    protected int window;
    private static final int count;
    protected short id;
    
    @Override
    public short id() {
        return this.id;
    }
    
    public int getWindow() {
        return this.window;
    }
    
    public short getId() {
        return this.id;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutTransaction.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public boolean valid() {
        return this.window == 0 && !this.accepted;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handleOut(this);
    }
    
    public boolean isAccepted() {
        return this.accepted;
    }
}
