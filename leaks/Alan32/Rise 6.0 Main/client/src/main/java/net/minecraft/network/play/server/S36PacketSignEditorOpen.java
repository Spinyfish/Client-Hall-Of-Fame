package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class S36PacketSignEditorOpen implements Packet<INetHandlerPlayClient> {
    private BlockPos signPosition;

    public S36PacketSignEditorOpen() {
    }

    public S36PacketSignEditorOpen(final BlockPos signPositionIn) {
        this.signPosition = signPositionIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleSignEditorOpen(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.signPosition = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.signPosition);
    }

    public BlockPos getSignPosition() {
        return this.signPosition;
    }
}
