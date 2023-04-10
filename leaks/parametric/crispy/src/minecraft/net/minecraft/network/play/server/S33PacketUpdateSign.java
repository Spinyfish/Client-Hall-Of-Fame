package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class S33PacketUpdateSign implements Packet
{
    private World world;
    private BlockPos blockPos;
    private IChatComponent[] lines;

    public S33PacketUpdateSign() {}

    public S33PacketUpdateSign(World worldIn, BlockPos blockPosIn, IChatComponent[] linesIn)
    {
        this.world = worldIn;
        this.blockPos = blockPosIn;
        this.lines = new IChatComponent[] {linesIn[0], linesIn[1], linesIn[2], linesIn[3]};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.blockPos = buf.readBlockPos();
        this.lines = new IChatComponent[4];

        for (int var2 = 0; var2 < 4; ++var2)
        {
            this.lines[var2] = buf.readChatComponent();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.blockPos);

        for (int var2 = 0; var2 < 4; ++var2)
        {
            buf.writeChatComponent(this.lines[var2]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUpdateSign(this);
    }

    public BlockPos getPos()
    {
        return this.blockPos;
    }

    public IChatComponent[] getLines()
    {
        return this.lines;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
