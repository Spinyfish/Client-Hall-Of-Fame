/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 22:28
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.*;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter @Setter
public class PacketEvent extends Event {
    private EventState state, direction;
    private Packet<?> packet;

    public <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }
}
