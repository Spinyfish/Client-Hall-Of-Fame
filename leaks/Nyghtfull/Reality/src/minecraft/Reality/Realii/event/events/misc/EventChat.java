/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.events.misc;

import Reality.Realii.event.Event;

public class EventChat
extends Event {
    private String message;

    public EventChat(String message) {
        this.message = message;
        this.setType((byte)0);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

