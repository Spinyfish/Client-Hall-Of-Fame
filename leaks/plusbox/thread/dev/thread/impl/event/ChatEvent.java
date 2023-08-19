package dev.thread.impl.event;

import dev.thread.api.event.EventCancellable;
import dev.thread.api.event.EventStage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
public class ChatEvent extends EventCancellable {
    private String message;

    public ChatEvent(String message){
        super(EventStage.NONE);
        this.message = message;
    }
}
