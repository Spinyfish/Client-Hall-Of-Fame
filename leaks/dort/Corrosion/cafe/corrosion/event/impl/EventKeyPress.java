package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventKeyPress extends Event {
    private final int keyCode;

    public int getKeyCode() {
        return this.keyCode;
    }

    public EventKeyPress(int keyCode) {
        this.keyCode = keyCode;
    }
}
