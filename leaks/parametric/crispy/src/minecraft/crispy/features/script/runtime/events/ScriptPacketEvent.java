package crispy.features.script.runtime.events;

import crispy.features.event.EventDirection;
import crispy.features.event.impl.player.EventPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.Packet;

import javax.xml.ws.RequestWrapper;

@RequiredArgsConstructor
@Getter
public class ScriptPacketEvent {
    private final Packet packet;
    @Setter
    private boolean cancelled;
    private final EventDirection direction;

    public void apply(EventPacket e) {
        e.setCancelled(cancelled);
    }
}
