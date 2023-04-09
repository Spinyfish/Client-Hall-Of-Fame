package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class MappedPacketSteerBoat implements Packet {
    private boolean left;
    private boolean right;

    public MappedPacketSteerBoat(boolean p_i46873_1_, boolean p_i46873_2_) {
        this.left = p_i46873_1_;
        this.right = p_i46873_2_;
    }


    public MappedPacketSteerBoat() {
    }


    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.left = buf.readBoolean();
        this.right = buf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeFloat(1);
        buf.writeFloat(1);
        buf.writeBoolean(this.left);
        buf.writeBoolean(this.right);
    }

    @Override
    public void processPacket(INetHandler var1) {

    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {

    }

    public boolean getLeft() {
        return this.left;
    }

    public boolean getRight() {
        return this.right;
    }
}
