package client.event.impl.motion;

import client.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class StrafeEvent extends CancellableEvent {
    private float strafe;

    private float forward;

    private float friction;

    private float rotationYaw;
}
