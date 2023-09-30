package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPosition;

public class S35PacketUpdateTileEntity implements Packet<INetHandlerPlayClient>
{
    private BlockPosition blockPosition;
    private int metadata;
    private NBTTagCompound nbt;

    public S35PacketUpdateTileEntity()
    {
    }

    public S35PacketUpdateTileEntity(BlockPosition blockPositionIn, int metadataIn, NBTTagCompound nbtIn)
    {
        this.blockPosition = blockPositionIn;
        this.metadata = metadataIn;
        this.nbt = nbtIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.blockPosition = buf.readBlockPos();
        this.metadata = buf.readUnsignedByte();
        this.nbt = buf.readNBTTagCompoundFromBuffer();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.blockPosition);
        buf.writeByte((byte)this.metadata);
        buf.writeNBTTagCompoundToBuffer(this.nbt);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUpdateTileEntity(this);
    }

    public BlockPosition getPos()
    {
        return this.blockPosition;
    }

    public int getTileEntityType()
    {
        return this.metadata;
    }

    public NBTTagCompound getNbtCompound()
    {
        return this.nbt;
    }
}
