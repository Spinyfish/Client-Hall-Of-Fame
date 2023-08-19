package dev.lemon.impl.event;

import dev.lemon.api.event.Event;
import dev.lemon.api.event.EventStage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyPressEvent extends Event {
    private int key;

    public KeyPressEvent(int key) {
        super(EventStage.NONE);
        this.key = key;
    }
}
