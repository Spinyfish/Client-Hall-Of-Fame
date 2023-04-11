package byron.Mono.event.impl;

import byron.Mono.event.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    public static Packet packet;
    public boolean cancelled;

    public EventPacket(Packet packet) {
        EventPacket.packet = packet;
    }

    public void setPacket(Packet packet) {
        EventPacket.packet = packet;
    }

    public static Packet getPacket() {
        return packet;
    }
}

