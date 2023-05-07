package client.event.impl.motion;

import client.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public final class MotionEvent extends CancellableEvent {
    public double posX;
    public double posY;
    public double posZ;
    public float rotationYaw;
    public float rotationPitch;
    public boolean onGround;
}
