package store.femboy.spring.impl.event.impl;

import net.minecraft.network.Packet;
import store.femboy.spring.impl.event.Event;

public final class EventGetPacket extends Event {

    public Packet packet;

    public EventGetPacket(Packet p) {
        this.packet = p;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet=packet;
    }
}
