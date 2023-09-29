package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient> {
    private String[] matches;

    public S3APacketTabComplete() {
    }

    public S3APacketTabComplete(final String[] matchesIn) {
        this.matches = matchesIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.matches = new String[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.matches.length; ++i) {
            this.matches[i] = buf.readStringFromBuffer(32767);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.matches.length);

        for (final String s : this.matches) {
            buf.writeString(s);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleTabComplete(this);
    }

    public String[] func_149630_c() {
        return this.matches;
    }
}
