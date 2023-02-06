package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event{
    public Packet packet;

    public EventPacket(Packet p)
    {
        this.packet = p;
    }

    public Packet getPacket()
    {
        return packet;
    }
}
