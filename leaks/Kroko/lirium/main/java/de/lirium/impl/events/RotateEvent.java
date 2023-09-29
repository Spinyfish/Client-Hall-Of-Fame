package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RotateEvent implements Event {
    public float curYaw, curPitch, deltaX, deltaY;
    public boolean stopRotate;
}
