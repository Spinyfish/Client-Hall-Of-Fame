package net.minecraft.network.play.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;
import java.util.List;

public class S30PacketWindowItems implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private ItemStack[] itemStacks;

    public S30PacketWindowItems() {
    }

    public S30PacketWindowItems(final int windowIdIn, final List<ItemStack> p_i45186_2_) {
        this.windowId = windowIdIn;
        this.itemStacks = new ItemStack[p_i45186_2_.size()];

        for (int i = 0; i < this.itemStacks.length; ++i) {
            final ItemStack itemstack = p_i45186_2_.get(i);
            this.itemStacks[i] = itemstack == null ? null : itemstack.copy();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.windowId = buf.readUnsignedByte();
        final int i = buf.readShort();
        this.itemStacks = new ItemStack[i];

        for (int j = 0; j < i; ++j) {
            this.itemStacks[j] = buf.readItemStackFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.itemStacks.length);

        for (final ItemStack itemstack : this.itemStacks) {
            buf.writeItemStackToBuffer(itemstack);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleWindowItems(this);
    }

    public int func_148911_c() {
        return this.windowId;
    }

    public ItemStack[] getItemStacks() {
        return this.itemStacks;
    }
}
