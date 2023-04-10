package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps implements Packet
{
    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public S34PacketMaps() {}

    public S34PacketMaps(int mapIdIn, byte scale, Collection visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY)
    {
        this.mapId = mapIdIn;
        this.mapScale = scale;
        this.mapVisiblePlayersVec4b = (Vec4b[])visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
        this.mapMinX = minX;
        this.mapMinY = minY;
        this.mapMaxX = maxX;
        this.mapMaxY = maxY;
        this.mapDataBytes = new byte[maxX * maxY];

        for (int var9 = 0; var9 < maxX; ++var9)
        {
            for (int var10 = 0; var10 < maxY; ++var10)
            {
                this.mapDataBytes[var9 + var10 * maxX] = colors[minX + var9 + (minY + var10) * 128];
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.mapId = buf.readVarIntFromBuffer();
        this.mapScale = buf.readByte();
        this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];

        for (int var2 = 0; var2 < this.mapVisiblePlayersVec4b.length; ++var2)
        {
            short var3 = (short)buf.readByte();
            this.mapVisiblePlayersVec4b[var2] = new Vec4b((byte)(var3 >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(var3 & 15));
        }

        this.mapMaxX = buf.readUnsignedByte();

        if (this.mapMaxX > 0)
        {
            this.mapMaxY = buf.readUnsignedByte();
            this.mapMinX = buf.readUnsignedByte();
            this.mapMinY = buf.readUnsignedByte();
            this.mapDataBytes = buf.readByteArray();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.mapId);
        buf.writeByte(this.mapScale);
        buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);
        Vec4b[] var2 = this.mapVisiblePlayersVec4b;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            Vec4b var5 = var2[var4];
            buf.writeByte((var5.func_176110_a() & 15) << 4 | var5.func_176111_d() & 15);
            buf.writeByte(var5.func_176112_b());
            buf.writeByte(var5.func_176113_c());
        }

        buf.writeByte(this.mapMaxX);

        if (this.mapMaxX > 0)
        {
            buf.writeByte(this.mapMaxY);
            buf.writeByte(this.mapMinX);
            buf.writeByte(this.mapMinY);
            buf.writeByteArray(this.mapDataBytes);
        }
    }

    public void handleClient(INetHandlerPlayClient handler)
    {
        handler.handleMaps(this);
    }

    public int getMapId()
    {
        return this.mapId;
    }

    /**
     * Sets new MapData from the packet to given MapData param
     */
    public void setMapdataTo(MapData mapdataIn)
    {
        mapdataIn.scale = this.mapScale;
        mapdataIn.playersVisibleOnMap.clear();
        int var2;

        for (var2 = 0; var2 < this.mapVisiblePlayersVec4b.length; ++var2)
        {
            Vec4b var3 = this.mapVisiblePlayersVec4b[var2];
            mapdataIn.playersVisibleOnMap.put("icon-" + var2, var3);
        }

        for (var2 = 0; var2 < this.mapMaxX; ++var2)
        {
            for (int var4 = 0; var4 < this.mapMaxY; ++var4)
            {
                mapdataIn.colors[this.mapMinX + var2 + (this.mapMinY + var4) * 128] = this.mapDataBytes[var2 + var4 * this.mapMaxX];
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.handleClient((INetHandlerPlayClient)handler);
    }
}
