package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems implements Packet
{
    private int windowId;
    private ItemStack[] itemStacks;

    public S30PacketWindowItems() {}

    public S30PacketWindowItems(int windowIdIn, List p_i45186_2_)
    {
        this.windowId = windowIdIn;
        this.itemStacks = new ItemStack[p_i45186_2_.size()];

        for (int var3 = 0; var3 < this.itemStacks.length; ++var3)
        {
            ItemStack var4 = (ItemStack)p_i45186_2_.get(var3);
            this.itemStacks[var3] = var4 == null ? null : var4.copy();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readUnsignedByte();
        short var2 = buf.readShort();
        this.itemStacks = new ItemStack[var2];

        for (int var3 = 0; var3 < var2; ++var3)
        {
            this.itemStacks[var3] = buf.readItemStackFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.itemStacks.length);
        ItemStack[] var2 = this.itemStacks;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];
            buf.writeItemStackToBuffer(var5);
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleWindowItems(this);
    }

    public int func_148911_c()
    {
        return this.windowId;
    }

    public ItemStack[] getItemStacks()
    {
        return this.itemStacks;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
