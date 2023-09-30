package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Setter
@Getter
@AllArgsConstructor
public class PacketEvent extends Event {
    private Packet packet;
    private final State state;

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public enum State {
        SEND, RECEIVE
    }
}
