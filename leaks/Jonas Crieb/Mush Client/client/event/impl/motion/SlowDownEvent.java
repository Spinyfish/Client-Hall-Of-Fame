package client.event.impl.motion;

import client.event.CancellableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SlowDownEvent extends CancellableEvent {
    public float strafeMultiplier;
    public float forwardMultiplier;
}