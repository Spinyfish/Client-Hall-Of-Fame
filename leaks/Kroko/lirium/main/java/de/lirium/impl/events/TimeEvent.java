package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TimeEvent implements Event {
    public long time;
}