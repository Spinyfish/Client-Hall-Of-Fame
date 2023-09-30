package felix.events.packet;

import felix.events.Cancellable;
import felix.events.Event;
import net.minecraft.network.Packet;

public final class EventPacketSend extends Cancellable implements Event  {
	
    private final Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> T getPacket() {
        return (T) packet;
    }
}
