package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

@Setter
public class S12PacketEntityVelocity implements Packet
{

    public int entityID;
    public int motionX;
    public int motionY;
    public int motionZ;

    public S12PacketEntityVelocity() {}

    public S12PacketEntityVelocity(Entity entityIn)
    {
        this(entityIn.getEntityId(), entityIn.motionX, entityIn.motionY, entityIn.motionZ);
    }

    public S12PacketEntityVelocity(int entityIDIn, double motionXIn, double motionYIn, double motionZIn)
    {
        this.entityID = entityIDIn;
        double var8 = 3.9D;

        if (motionXIn < -var8)
        {
            motionXIn = -var8;
        }

        if (motionYIn < -var8)
        {
            motionYIn = -var8;
        }

        if (motionZIn < -var8)
        {
            motionZIn = -var8;
        }

        if (motionXIn > var8)
        {
            motionXIn = var8;
        }

        if (motionYIn > var8)
        {
            motionYIn = var8;
        }

        if (motionZIn > var8)
        {
            motionZIn = var8;
        }

        this.motionX = (int)(motionXIn * 8000.0D);
        this.motionY = (int)(motionYIn * 8000.0D);
        this.motionZ = (int)(motionZIn * 8000.0D);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityID = buf.readVarIntFromBuffer();
        this.motionX = buf.readShort();
        this.motionY = buf.readShort();
        this.motionZ = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityID);
        buf.writeShort(this.motionX);
        buf.writeShort(this.motionY);
        buf.writeShort(this.motionZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityVelocity(this);
    }

    public int getEntityID()
    {
        return this.entityID;
    }

    public int getMotionX()
    {
        return this.motionX;
    }

    public int getMotionY()
    {
        return this.motionY;
    }

    public int getMotionZ()
    {
        return this.motionZ;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
