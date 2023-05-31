package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LightMapEvent implements Event {
    public float red, green, blue;
    public boolean overwrite;
}
