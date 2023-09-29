package juul.event;

import net.minecraft.network.Packet;

public class EventPacket extends Event<EventPacket>
{

    Packet packet;
    TrafficDirection direction;

    EventPacket(Packet p, TrafficDirection pp)
    {
        this.packet = p;
        this.direction = pp;
    }

    public Packet getPacket() { return this.packet; }

    public boolean incoming() { return this.direction == TrafficDirection.INCOMING; }
    public boolean outgoing() { return this.direction == TrafficDirection.OUTGOING; }

    public enum TrafficDirection {
        INCOMING,
        OUTGOING
    }

}
