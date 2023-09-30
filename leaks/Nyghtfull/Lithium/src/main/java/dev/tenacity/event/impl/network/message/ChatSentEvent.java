package dev.tenacity.event.impl.network.message;

import dev.tenacity.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class ChatSentEvent extends Event {
    private final String message;

    public ChatSentEvent(String message) {
        this.message = message;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getMessage() {
        return message;
    }

}
