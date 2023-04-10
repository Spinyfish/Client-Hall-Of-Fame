package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities implements Packet
{
    private int[] entityIDs;

    public S13PacketDestroyEntities() {}

    public S13PacketDestroyEntities(int ... entityIDsIn)
    {
        this.entityIDs = entityIDsIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityIDs = new int[buf.readVarIntFromBuffer()];

        for (int var2 = 0; var2 < this.entityIDs.length; ++var2)
        {
            this.entityIDs[var2] = buf.readVarIntFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityIDs.length);

        for (int var2 = 0; var2 < this.entityIDs.length; ++var2)
        {
            buf.writeVarIntToBuffer(this.entityIDs[var2]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleDestroyEntities(this);
    }

    public int[] getEntityIDs()
    {
        return this.entityIDs;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
