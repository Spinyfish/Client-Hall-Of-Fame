package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S09PacketHeldItemChange implements Packet
{
    private int heldItemHotbarIndex;

    public S09PacketHeldItemChange() {}

    public S09PacketHeldItemChange(int hotbarIndexIn)
    {
        this.heldItemHotbarIndex = hotbarIndexIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.heldItemHotbarIndex = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.heldItemHotbarIndex);
    }

    public void processPacket(INetHandlerPlayClient p_180746_1_)
    {
        p_180746_1_.handleHeldItemChange(this);
    }

    public int getHeldItemHotbarIndex()
    {
        return this.heldItemHotbarIndex;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
