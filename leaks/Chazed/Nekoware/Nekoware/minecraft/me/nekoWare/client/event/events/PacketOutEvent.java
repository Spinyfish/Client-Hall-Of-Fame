
package me.nekoWare.client.event.events;

import java.util.List;

import me.nekoWare.client.event.Event;
import net.minecraft.network.Packet;

public class PacketOutEvent extends Event {
    private Packet packet;

    public PacketOutEvent(final Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet packet) {
        this.packet = packet;
    }

    public void queueAndCancel(final List<Packet> list) {
        list.add(this.packet);
        this.cancel();
    }
}
