package dev.tenacity.event.impl.network;

import dev.tenacity.event.Event;
import net.minecraft.network.Packet;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class PacketEvent extends Event {
    private Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public Packet<?> getPacket() {
        return packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getPacketID() {
        return getPacket().getID();
    }

}
