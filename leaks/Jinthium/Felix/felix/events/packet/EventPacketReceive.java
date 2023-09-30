package felix.events.packet;

import felix.events.Cancellable;
import felix.events.Event;
import net.minecraft.network.Packet;

public final class EventPacketReceive extends Cancellable implements Event {
	
    private final Packet packet;

    public EventPacketReceive(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}
