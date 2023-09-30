package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C00PacketKeepAlive implements Packet<INetHandlerPlayServer> {
    public int key;

    public C00PacketKeepAlive() {
    }

    public C00PacketKeepAlive(final int key) {
        this.key = key;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processKeepAlive(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.key = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.key);
    }

    public int getKey() {
        return this.key;
    }
}
