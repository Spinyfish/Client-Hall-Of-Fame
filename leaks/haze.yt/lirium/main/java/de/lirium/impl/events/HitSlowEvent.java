package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class HitSlowEvent implements Event {
    public boolean unSprint;
    public double slowdown;
}