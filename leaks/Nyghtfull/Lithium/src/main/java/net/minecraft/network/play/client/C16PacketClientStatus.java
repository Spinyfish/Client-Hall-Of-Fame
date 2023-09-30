package net.minecraft.network.play.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C16PacketClientStatus implements Packet<INetHandlerPlayServer> {
    private C16PacketClientStatus.EnumState status;

    public C16PacketClientStatus() {
    }

    public C16PacketClientStatus(C16PacketClientStatus.EnumState statusIn) {
        this.status = statusIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.status = (C16PacketClientStatus.EnumState) buf.readEnumValue(C16PacketClientStatus.EnumState.class);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.status);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClientStatus(this);
    }

    public C16PacketClientStatus.EnumState getStatus() {
        return this.status;
    }

    @Getter
    @AllArgsConstructor
    public enum EnumState {
        PERFORM_RESPAWN(0),
        REQUEST_STATS(1),
        OPEN_INVENTORY_ACHIEVEMENT(2);
        private final int id;
    }


    @Override
    public int getID() {
        return 22;
    }

}
