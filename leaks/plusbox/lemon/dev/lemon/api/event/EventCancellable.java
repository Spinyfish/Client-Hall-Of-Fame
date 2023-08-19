package dev.lemon.api.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCancellable extends Event {
    private boolean cancelled;

    public EventCancellable(EventStage stage) {
        super(stage);
    }
}