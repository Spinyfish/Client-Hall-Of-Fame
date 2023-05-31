package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrientCameraRotateEvent extends CancellableEvent {
    public float yaw, pitch;
}
