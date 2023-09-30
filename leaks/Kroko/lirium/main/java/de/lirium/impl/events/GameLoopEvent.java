package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class GameLoopEvent implements Event {
    public boolean capTicks;
}