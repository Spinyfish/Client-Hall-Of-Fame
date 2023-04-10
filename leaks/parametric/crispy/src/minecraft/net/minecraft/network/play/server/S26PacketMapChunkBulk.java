package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet
{
    private int[] xPositions;
    private int[] zPositions;
    private S21PacketChunkData.Extracted[] chunksData;
    private boolean isOverworld;

    public S26PacketMapChunkBulk() {}

    public S26PacketMapChunkBulk(List chunks)
    {
        int var2 = chunks.size();
        this.xPositions = new int[var2];
        this.zPositions = new int[var2];
        this.chunksData = new S21PacketChunkData.Extracted[var2];
        this.isOverworld = !((Chunk)chunks.get(0)).getWorld().provider.getHasNoSky();

        for (int var3 = 0; var3 < var2; ++var3)
        {
            Chunk var4 = (Chunk)chunks.get(var3);
            S21PacketChunkData.Extracted var5 = S21PacketChunkData.func_179756_a(var4, true, this.isOverworld, 65535);
            this.xPositions[var3] = var4.xPosition;
            this.zPositions[var3] = var4.zPosition;
            this.chunksData[var3] = var5;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.isOverworld = buf.readBoolean();
        int var2 = buf.readVarIntFromBuffer();
        this.xPositions = new int[var2];
        this.zPositions = new int[var2];
        this.chunksData = new S21PacketChunkData.Extracted[var2];
        int var3;

        for (var3 = 0; var3 < var2; ++var3)
        {
            this.xPositions[var3] = buf.readInt();
            this.zPositions[var3] = buf.readInt();
            this.chunksData[var3] = new S21PacketChunkData.Extracted();
            this.chunksData[var3].dataSize = buf.readShort() & 65535;
            this.chunksData[var3].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[var3].dataSize), this.isOverworld, true)];
        }

        for (var3 = 0; var3 < var2; ++var3)
        {
            buf.readBytes(this.chunksData[var3].data);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBoolean(this.isOverworld);
        buf.writeVarIntToBuffer(this.chunksData.length);
        int var2;

        for (var2 = 0; var2 < this.xPositions.length; ++var2)
        {
            buf.writeInt(this.xPositions[var2]);
            buf.writeInt(this.zPositions[var2]);
            buf.writeShort((short)(this.chunksData[var2].dataSize & 65535));
        }

        for (var2 = 0; var2 < this.xPositions.length; ++var2)
        {
            buf.writeBytes(this.chunksData[var2].data);
        }
    }

    public void handleMapChunkBulk(INetHandlerPlayClient handler)
    {
        handler.handleMapChunkBulk(this);
    }

    public int getChunkX(int p_149255_1_)
    {
        return this.xPositions[p_149255_1_];
    }

    public int getChunkZ(int p_149253_1_)
    {
        return this.zPositions[p_149253_1_];
    }

    public int getChunkCount()
    {
        return this.xPositions.length;
    }

    public byte[] getChunkBytes(int p_149256_1_)
    {
        return this.chunksData[p_149256_1_].data;
    }

    public int getChunkSize(int p_179754_1_)
    {
        return this.chunksData[p_179754_1_].dataSize;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.handleMapChunkBulk((INetHandlerPlayClient)handler);
    }
}
