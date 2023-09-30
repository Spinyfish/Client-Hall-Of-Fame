package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.io.IOException;
import java.util.List;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
    public double posX;
    public double posY;
    public double posZ;
    private float strength;
    private List<BlockPos> affectedBlockPositions;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;

    public S27PacketExplosion() {
    }

    public S27PacketExplosion(final double p_i45193_1_, final double y, final double z, final float strengthIn, final List<BlockPos> affectedBlocksIn, final Vec3 p_i45193_9_) {
        this.posX = p_i45193_1_;
        this.posY = y;
        this.posZ = z;
        this.strength = strengthIn;
        this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);

        if (p_i45193_9_ != null) {
            this.field_149152_f = (float) p_i45193_9_.xCoord;
            this.field_149153_g = (float) p_i45193_9_.yCoord;
            this.field_149159_h = (float) p_i45193_9_.zCoord;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.strength = buf.readFloat();
        final int i = buf.readInt();
        this.affectedBlockPositions = Lists.newArrayListWithCapacity(i);
        final int j = (int) this.posX;
        final int k = (int) this.posY;
        final int l = (int) this.posZ;

        for (int i1 = 0; i1 < i; ++i1) {
            final int j1 = buf.readByte() + j;
            final int k1 = buf.readByte() + k;
            final int l1 = buf.readByte() + l;
            this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
        }

        this.field_149152_f = buf.readFloat();
        this.field_149153_g = buf.readFloat();
        this.field_149159_h = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeFloat((float) this.posX);
        buf.writeFloat((float) this.posY);
        buf.writeFloat((float) this.posZ);
        buf.writeFloat(this.strength);
        buf.writeInt(this.affectedBlockPositions.size());
        final int i = (int) this.posX;
        final int j = (int) this.posY;
        final int k = (int) this.posZ;

        for (final BlockPos blockpos : this.affectedBlockPositions) {
            final int l = blockpos.getX() - i;
            final int i1 = blockpos.getY() - j;
            final int j1 = blockpos.getZ() - k;
            buf.writeByte(l);
            buf.writeByte(i1);
            buf.writeByte(j1);
        }

        buf.writeFloat(this.field_149152_f);
        buf.writeFloat(this.field_149153_g);
        buf.writeFloat(this.field_149159_h);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleExplosion(this);
    }

    public float func_149149_c() {
        return this.field_149152_f;
    }

    public float func_149144_d() {
        return this.field_149153_g;
    }

    public float func_149147_e() {
        return this.field_149159_h;
    }

    public double getX() {
        return this.posX;
    }

    public double getY() {
        return this.posY;
    }

    public double getZ() {
        return this.posZ;
    }

    public float getStrength() {
        return this.strength;
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }
}
