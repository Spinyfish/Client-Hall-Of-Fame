/**
 * @project Myth
 * @author Skush/Duzey
 * @at 06.08.2022
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import dev.myth.api.event.EventState;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    @Setter private Packet<?> packet;
    @Getter @Setter private EventState state, timing;

    public PacketEvent(final EventState state, final EventState timing, final Packet<?> packet) {
        this.packet = packet;
        this.state = state;
        this.timing = timing;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }
}
