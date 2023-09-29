package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MouseOverEvent implements Event {
    public double maxRange, blockReach;
}
