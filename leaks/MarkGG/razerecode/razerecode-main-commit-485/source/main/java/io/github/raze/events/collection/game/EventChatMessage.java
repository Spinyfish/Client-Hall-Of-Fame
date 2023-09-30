package io.github.raze.events.collection.game;

import io.github.raze.events.system.BaseEvent;

public class EventChatMessage extends BaseEvent {

    public String message;

    public EventChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
