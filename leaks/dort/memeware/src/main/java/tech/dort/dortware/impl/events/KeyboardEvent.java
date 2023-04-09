package tech.dort.dortware.impl.events;

import tech.dort.dortware.api.event.Event;

public class KeyboardEvent extends Event {

    private final int key;

    public KeyboardEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
