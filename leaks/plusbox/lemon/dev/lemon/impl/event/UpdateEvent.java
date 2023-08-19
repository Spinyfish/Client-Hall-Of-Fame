package dev.lemon.impl.event;

import dev.lemon.api.event.Event;
import dev.lemon.api.event.EventStage;

public class UpdateEvent extends Event {
    public UpdateEvent() {
        super(EventStage.NONE);
    }
}
