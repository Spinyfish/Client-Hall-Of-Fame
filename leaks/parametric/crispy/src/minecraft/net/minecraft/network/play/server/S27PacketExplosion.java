package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet
{
    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private List affectedBlockPositions;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;

    public S27PacketExplosion() {}

    public S27PacketExplosion(double p_i45193_1_, double y, double z, float strengthIn, List affectedBlocksIn, Vec3 p_i45193_9_)
    {
        this.posX = p_i45193_1_;
        this.posY = y;
        this.posZ = z;
        this.strength = strengthIn;
        this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);

        if (p_i45193_9_ != null)
        {
            this.field_149152_f = (float)p_i45193_9_.xCoord;
            this.field_149153_g = (float)p_i45193_9_.yCoord;
            this.field_149159_h = (float)p_i45193_9_.zCoord;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.posX = (double)buf.readFloat();
        this.posY = (double)buf.readFloat();
        this.posZ = (double)buf.readFloat();
        this.strength = buf.readFloat();
        int var2 = buf.readInt();
        this.affectedBlockPositions = Lists.newArrayListWithCapacity(var2);
        int var3 = (int)this.posX;
        int var4 = (int)this.posY;
        int var5 = (int)this.posZ;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            int var7 = buf.readByte() + var3;
            int var8 = buf.readByte() + var4;
            int var9 = buf.readByte() + var5;
            this.affectedBlockPositions.add(new BlockPos(var7, var8, var9));
        }

        this.field_149152_f = buf.readFloat();
        this.field_149153_g = buf.readFloat();
        this.field_149159_h = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeFloat((float)this.posX);
        buf.writeFloat((float)this.posY);
        buf.writeFloat((float)this.posZ);
        buf.writeFloat(this.strength);
        buf.writeInt(this.affectedBlockPositions.size());
        int var2 = (int)this.posX;
        int var3 = (int)this.posY;
        int var4 = (int)this.posZ;
        Iterator var5 = this.affectedBlockPositions.iterator();

        while (var5.hasNext())
        {
            BlockPos var6 = (BlockPos)var5.next();
            int var7 = var6.getX() - var2;
            int var8 = var6.getY() - var3;
            int var9 = var6.getZ() - var4;
            buf.writeByte(var7);
            buf.writeByte(var8);
            buf.writeByte(var9);
        }

        buf.writeFloat(this.field_149152_f);
        buf.writeFloat(this.field_149153_g);
        buf.writeFloat(this.field_149159_h);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleExplosion(this);
    }

    public float func_149149_c()
    {
        return this.field_149152_f;
    }

    public float func_149144_d()
    {
        return this.field_149153_g;
    }

    public float func_149147_e()
    {
        return this.field_149159_h;
    }

    public double getX()
    {
        return this.posX;
    }

    public double getY()
    {
        return this.posY;
    }

    public double getZ()
    {
        return this.posZ;
    }

    public float getStrength()
    {
        return this.strength;
    }

    public List getAffectedBlockPositions()
    {
        return this.affectedBlockPositions;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
