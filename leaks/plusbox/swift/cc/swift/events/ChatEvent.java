package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class ChatEvent extends Event {
    private String text;
    private boolean cancelled;
    public ChatEvent(String text) {
        this.text = text;
    }
}
