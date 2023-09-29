package com.darkmagician6.eventapi.events.callables;

import net.minecraft.network.Packet;

public class EventReceivedPacket extends EventCancellable {

    private Packet packet;

    public EventReceivedPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

}
