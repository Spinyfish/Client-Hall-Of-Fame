package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.attribute.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketOut extends Event implements EventCancellable {
    private Packet<?> packet;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public <T extends Packet<?>> T getPacket() {
        return this.packet;
    }

    public EventPacketOut(Packet<?> packet) {
        this.packet = packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
