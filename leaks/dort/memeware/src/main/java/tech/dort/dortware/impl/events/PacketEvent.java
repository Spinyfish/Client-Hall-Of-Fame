package tech.dort.dortware.impl.events;

import net.minecraft.network.Packet;
import tech.dort.dortware.api.event.Event;
import tech.dort.dortware.impl.events.enums.PacketDirection;

public class PacketEvent extends Event {

    private final PacketDirection packetDirection;
    private Packet packet;

    public PacketEvent(PacketDirection packetDirection, Packet packet) {
        this.packetDirection = packetDirection;
        this.packet = packet;
    }

    public PacketDirection getPacketDirection() {
        return packetDirection;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Class getPacketClass() {
        return packet.getClass();
    }
}
