package byron.Mono.event.impl;

import byron.Mono.event.Event;

public class EventChat extends Event { // This one is 100% implomented.

    private String message;

    public EventChat(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
