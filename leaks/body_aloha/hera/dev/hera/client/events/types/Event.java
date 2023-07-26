package dev.hera.client.events.types;

import dev.hera.client.events.EventBus;

public class Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Event post() {
        EventBus.post(this);
        return this;
    }
}