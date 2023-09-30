/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1DPacketEntityEffect
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte hideParticles;

    public S1DPacketEntityEffect() {
    }

    public S1DPacketEntityEffect(int entityIdIn, PotionEffect effect) {
        this.entityId = entityIdIn;
        this.effectId = (byte)(effect.getPotionID() & 0xFF);
        this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
        this.duration = effect.getDuration() > Short.MAX_VALUE ? Short.MAX_VALUE : effect.getDuration();
        this.hideParticles = (byte)(effect.getIsShowParticles() ? 1 : 0);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarIntFromBuffer();
        this.effectId = buf.readByte();
        this.amplifier = buf.readByte();
        this.duration = buf.readVarIntFromBuffer();
        this.hideParticles = buf.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        buf.writeVarIntToBuffer(this.duration);
        buf.writeByte(this.hideParticles);
    }

    public boolean func_149429_c() {
        return this.duration == Short.MAX_VALUE;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean func_179707_f() {
        return this.hideParticles != 0;
    }
}

