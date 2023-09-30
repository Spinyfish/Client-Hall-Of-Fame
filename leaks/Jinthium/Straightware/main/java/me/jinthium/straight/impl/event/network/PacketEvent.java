package me.jinthium.straight.impl.event.network;

import me.jinthium.straight.api.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private PacketState packetState;
    private Packet<?> packet;

    public PacketEvent(PacketState packetState, Packet<?> packet) {
        this.packetState = packetState;
        this.packet = packet;
    }

    public PacketState getPacketState() {
        return packetState;
    }

    public void setPacketState(PacketState packetState) {
        this.packetState = packetState;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>> T getPacket(){
        return (T)packet;
    }

    public enum PacketState {
        SENDING,
        RECEIVING,
        ADDQUEUE
    }
}
