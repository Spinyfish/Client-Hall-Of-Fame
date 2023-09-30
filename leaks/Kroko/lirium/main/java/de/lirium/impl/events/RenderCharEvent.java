package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RenderCharEvent implements Event {
    public boolean skip;
    public char character;
}
