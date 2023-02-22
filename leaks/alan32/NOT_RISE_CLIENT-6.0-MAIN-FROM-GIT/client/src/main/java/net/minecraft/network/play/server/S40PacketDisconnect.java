package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

import java.io.IOException;

public class S40PacketDisconnect implements Packet<INetHandlerPlayClient> {
    private IChatComponent reason;

    public S40PacketDisconnect() {
    }

    public S40PacketDisconnect(final IChatComponent reasonIn) {
        this.reason = reasonIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.reason = buf.readChatComponent();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeChatComponent(this.reason);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleDisconnect(this);
    }

    public IChatComponent getReason() {
        return this.reason;
    }
}
