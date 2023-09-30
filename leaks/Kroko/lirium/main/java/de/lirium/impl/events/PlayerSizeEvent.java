package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerSizeEvent implements Event {
    public float size;
}
