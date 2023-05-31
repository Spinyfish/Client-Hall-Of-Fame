package de.lirium.impl.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatEvent extends CancellableEvent {
    public String message;
}