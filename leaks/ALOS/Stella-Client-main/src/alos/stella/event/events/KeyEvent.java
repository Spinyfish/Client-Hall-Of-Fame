package alos.stella.event.events;

import alos.stella.event.Event;

public final class KeyEvent extends Event {
    private final int key;

    public final int getKey() {
        return this.key;
    }

    public KeyEvent(int key) {
        this.key = key;
    }
}