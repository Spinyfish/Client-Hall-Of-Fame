package rip.athena.client.events.types.network;

import rip.athena.client.events.*;
import net.minecraft.network.*;

public class IngoingPacketEvent extends Event
{
    private Packet<?> packet;
    
    public IngoingPacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }
    
    public Packet<?> getPacket() {
        return this.packet;
    }
}
