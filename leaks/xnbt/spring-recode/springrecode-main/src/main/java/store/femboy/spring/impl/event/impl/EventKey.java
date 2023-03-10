package store.femboy.spring.impl.event.impl;

import store.femboy.spring.impl.event.Event;

public final class EventKey extends Event {
    public int key;

    public EventKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
