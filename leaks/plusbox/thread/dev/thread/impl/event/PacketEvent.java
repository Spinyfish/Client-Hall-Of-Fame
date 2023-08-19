package dev.thread.impl.event;

import dev.thread.api.event.Event;
import dev.thread.api.event.EventCancellable;
import dev.thread.api.event.EventStage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
public class PacketEvent extends EventCancellable {
    private Packet<?> packet;

    public PacketEvent(Packet<?> packet){
        super(EventStage.NONE);
        this.packet = packet;
    }
}
