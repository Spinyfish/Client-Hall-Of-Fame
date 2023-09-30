package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPosition;

import java.io.IOException;

public class S05PacketSpawnPosition implements Packet<INetHandlerPlayClient>
{
    private BlockPosition spawnBlockPosition;

    public S05PacketSpawnPosition()
    {
    }

    public S05PacketSpawnPosition(BlockPosition spawnBlockPositionIn)
    {
        this.spawnBlockPosition = spawnBlockPositionIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.spawnBlockPosition = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.spawnBlockPosition);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPosition(this);
    }

    public BlockPosition getSpawnPos()
    {
        return this.spawnBlockPosition;
    }
}
