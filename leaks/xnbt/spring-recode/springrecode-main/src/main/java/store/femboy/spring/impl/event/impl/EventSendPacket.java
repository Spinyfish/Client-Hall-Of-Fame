package store.femboy.spring.impl.event.impl;

import net.minecraft.network.Packet;
import store.femboy.spring.impl.event.Event;

public final class EventSendPacket extends Event {
    public Packet packet;

    public EventSendPacket(Packet p) {
        this.packet = p;
    }

    public Packet getPacket() {
        return packet;
    }
}
