package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.transaction.effects.*;

public abstract class VPacketPlayOutRemoveEntityEffect extends VPacket implements Effectable
{
    private static final int count;
    protected int id;
    protected int effect;
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public RemovedEffect toEffect() {
        return new RemovedEffect(this.effect);
    }
    
    @Override
    public EffectUpdate toEffect() {
        return (EffectUpdate)this.toEffect();
    }
    
    public int getEffect() {
        return this.effect;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutRemoveEntityEffect.count;
    }
}
