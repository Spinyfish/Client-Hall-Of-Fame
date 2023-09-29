package net.minecraft.network.play.client;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;

import java.io.IOException;

@Setter@Getter
public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
    private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
    private BlockPos position;
    private int placedBlockDirection;
    private ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public C08PacketPlayerBlockPlacement() {
    }

    public C08PacketPlayerBlockPlacement(final ItemStack stackIn) {
        this(field_179726_a, 255, stackIn, 0.0F, 0.0F, 0.0F);
    }

    public C08PacketPlayerBlockPlacement(final BlockPos positionIn, final int placedBlockDirectionIn, final ItemStack stackIn, final float facingXIn, final float facingYIn, final float facingZIn) {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.position = buf.readBlockPos();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStackFromBuffer();
        this.facingX = (float) buf.readUnsignedByte() / 16.0F;
        this.facingY = (float) buf.readUnsignedByte() / 16.0F;
        this.facingZ = (float) buf.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.position);
        buf.writeByte(this.placedBlockDirection);
        buf.writeItemStackToBuffer(this.stack);
        buf.writeByte((int) (this.facingX * 16.0F));
        buf.writeByte((int) (this.facingY * 16.0F));
        buf.writeByte((int) (this.facingZ * 16.0F));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processPlayerBlockPlacement(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public int getPlacedBlockDirection() {
        return this.placedBlockDirection;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    /**
     * Returns the offset from xPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetX() {
        return this.facingX;
    }

    /**
     * Returns the offset from yPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetY() {
        return this.facingY;
    }

    /**
     * Returns the offset from zPosition where the actual click took place.
     */
    public float getPlacedBlockOffsetZ() {
        return this.facingZ;
    }
}
