package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient> {
    private String channel;
    private PacketBuffer data;

    public S3FPacketCustomPayload() {
    }

    public S3FPacketCustomPayload(final String channelName, final PacketBuffer dataIn) {
        this.channel = channelName;
        this.data = dataIn;

        if (dataIn.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.channel = buf.readStringFromBuffer(20);
        final int i = buf.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.data = new PacketBuffer(buf.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.channel);
        buf.writeBytes(this.data);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleCustomPayload(this);
    }

    public String getChannelName() {
        return this.channel;
    }

    public PacketBuffer getBufferData() {
        return this.data;
    }
}
