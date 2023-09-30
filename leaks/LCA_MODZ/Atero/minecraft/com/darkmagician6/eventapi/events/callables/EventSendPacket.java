package com.darkmagician6.eventapi.events.callables;

import net.minecraft.network.Packet;

public class EventSendPacket extends EventCancellable {

    private Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
