package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;
import dev.elysium.client.Elysium;

public class EventChat extends Event {

    public String message;

    public EventChat(String m) {
        this.message = m;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
