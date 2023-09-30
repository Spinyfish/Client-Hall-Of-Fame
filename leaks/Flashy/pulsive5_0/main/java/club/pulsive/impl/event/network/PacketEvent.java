package club.pulsive.impl.event.network;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class PacketEvent extends Event {
    private final EventState eventState;
    private Packet packet;

    public enum EventState{
        RECEIVING,
        SENDING
    }
    public <T extends Packet> T getPacket() {
        return (T) packet;
    }
}
