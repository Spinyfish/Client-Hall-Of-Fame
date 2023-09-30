package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PosLookEvent implements Event {
    public float yaw, pitch;
}
