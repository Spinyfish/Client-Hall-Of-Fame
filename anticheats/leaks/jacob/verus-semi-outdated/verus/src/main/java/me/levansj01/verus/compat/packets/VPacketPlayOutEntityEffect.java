package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.data.transaction.effects.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayOutEntityEffect extends VPacket implements Effectable
{
    protected byte effect;
    protected int id;
    protected byte amplifier;
    protected int duration;
    private static final int count;
    protected byte flags;
    
    public byte getEffect() {
        return this.effect;
    }
    
    @Override
    public EffectUpdate toEffect() {
        return (EffectUpdate)this.toEffect();
    }
    
    public byte getAmplifier() {
        return this.amplifier;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutEntityEffect.count;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public byte getFlags() {
        return this.flags;
    }
    
    public Effect toEffect() {
        return new Effect(this.duration, this.effect, this.amplifier);
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}
