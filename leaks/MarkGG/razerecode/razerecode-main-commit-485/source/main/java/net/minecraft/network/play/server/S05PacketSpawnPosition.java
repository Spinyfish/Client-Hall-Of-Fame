package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPosition;

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

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.spawnBlockPosition = buf.readBlockPos();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.spawnBlockPosition);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPosition(this);
    }

    public BlockPosition getSpawnPos()
    {
        return this.spawnBlockPosition;
    }
}
