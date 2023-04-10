package crispy.features.event.impl.player;

import crispy.features.event.Event;
import crispy.features.event.EventDirection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@RequiredArgsConstructor
public class EventPacket extends Event<EventPacket> {
    private final Packet packet;
    private final EventDirection direction;
    @Setter
    private boolean cancelled;


}

