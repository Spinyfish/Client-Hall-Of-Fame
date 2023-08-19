package dev.thread.impl.event;

import dev.thread.api.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyPressEvent extends Event {
    private int key;

    public KeyPressEvent(int key) {
        this.key = key;
    }
}
