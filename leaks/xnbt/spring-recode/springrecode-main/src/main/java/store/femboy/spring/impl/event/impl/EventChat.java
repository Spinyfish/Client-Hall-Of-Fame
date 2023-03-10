package store.femboy.spring.impl.event.impl;

import store.femboy.spring.impl.event.Event;

public final class EventChat extends Event {

    public String message;

    public EventChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
