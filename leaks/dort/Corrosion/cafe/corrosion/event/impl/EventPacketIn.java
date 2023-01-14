package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.attribute.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketIn extends Event implements EventCancellable {
    private final Packet<?> packet;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public <T extends Packet<?>> T getPacket() {
        return this.packet;
    }

    public EventPacketIn(Packet<?> packet) {
        this.packet = packet;
    }
}
