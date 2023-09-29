package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S03PacketTimeUpdate implements Packet<INetHandlerPlayClient> {
    private long totalWorldTime;
    private long worldTime;

    public S03PacketTimeUpdate() {
    }

    public S03PacketTimeUpdate(final long totalWorldTimeIn, final long totalTimeIn, final boolean doDayLightCycle) {
        this.totalWorldTime = totalWorldTimeIn;
        this.worldTime = totalTimeIn;

        if (!doDayLightCycle) {
            this.worldTime = -this.worldTime;

            if (this.worldTime == 0L) {
                this.worldTime = -1L;
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.totalWorldTime = buf.readLong();
        this.worldTime = buf.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeLong(this.totalWorldTime);
        buf.writeLong(this.worldTime);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleTimeUpdate(this);
    }

    public long getTotalWorldTime() {
        return this.totalWorldTime;
    }

    public long getWorldTime() {
        return this.worldTime;
    }
}
