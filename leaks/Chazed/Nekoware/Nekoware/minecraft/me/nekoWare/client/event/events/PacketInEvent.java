
package me.nekoWare.client.event.events;

import me.nekoWare.client.event.Event;
import net.minecraft.network.Packet;

public class PacketInEvent extends Event {
    private Packet packet;

    public PacketInEvent(final Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet packet) {
        this.packet = packet;
    }
}
