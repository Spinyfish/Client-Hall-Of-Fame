package me.kansio.client.event.impl;

import me.kansio.client.event.Event;
import me.kansio.client.event.PacketDirection;
import net.minecraft.network.Packet;

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