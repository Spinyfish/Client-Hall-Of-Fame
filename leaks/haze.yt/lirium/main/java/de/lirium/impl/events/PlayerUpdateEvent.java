package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerUpdateEvent implements Event {
    private double x, y, z;
    private boolean onGround, sprinting, sneaking;
    private State state;

    public enum State {
        UPDATE, PRE, POST;
    }
}