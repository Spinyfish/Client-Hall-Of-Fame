package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AttackEntityEvent implements Event {

    public State state;

    public enum State {
        BEFORE, AFTER;
    }
}
