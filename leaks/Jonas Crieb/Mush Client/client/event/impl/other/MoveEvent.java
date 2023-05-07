package client.event.impl.other;

import client.event.CancellableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class MoveEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;
}
