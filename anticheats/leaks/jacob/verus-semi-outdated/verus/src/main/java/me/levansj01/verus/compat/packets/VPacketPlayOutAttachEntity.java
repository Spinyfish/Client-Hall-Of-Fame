package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutAttachEntity extends VPacket
{
    protected int entityId;
    protected int attachId;
    private static final int count;
    protected byte leash;
    
    public byte getLeash() {
        return this.leash;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    static {
        count = VPacket.count();
    }
    
    public int getAttachId() {
        return this.attachId;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutAttachEntity.count;
    }
}
