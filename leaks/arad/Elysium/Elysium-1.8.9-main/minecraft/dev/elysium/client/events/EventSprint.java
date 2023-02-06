package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;

public class EventSprint extends Event {
    boolean sprint;

    public boolean shouldSprint() {
        return sprint;
    }

    public void setSprinting(boolean sprint) {
        this.sprint = sprint;
    }

    public EventSprint(boolean sprint) {
        this.sprint = sprint;
    }
}
