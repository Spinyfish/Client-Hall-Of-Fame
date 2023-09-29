package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S1FPacketSetExperience implements Packet<INetHandlerPlayClient> {
    private float field_149401_a;
    private int totalExperience;
    private int level;

    public S1FPacketSetExperience() {
    }

    public S1FPacketSetExperience(final float p_i45222_1_, final int totalExperienceIn, final int levelIn) {
        this.field_149401_a = p_i45222_1_;
        this.totalExperience = totalExperienceIn;
        this.level = levelIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.field_149401_a = buf.readFloat();
        this.level = buf.readVarIntFromBuffer();
        this.totalExperience = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeFloat(this.field_149401_a);
        buf.writeVarIntToBuffer(this.level);
        buf.writeVarIntToBuffer(this.totalExperience);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleSetExperience(this);
    }

    public float func_149397_c() {
        return this.field_149401_a;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getLevel() {
        return this.level;
    }
}
