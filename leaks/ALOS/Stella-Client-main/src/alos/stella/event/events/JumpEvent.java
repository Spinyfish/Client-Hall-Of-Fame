package alos.stella.event.events;

import alos.stella.event.CancellableEvent;

public final class JumpEvent extends CancellableEvent {
    private float motion;

    public final float getMotion() {
        return this.motion;
    }

    public final void setMotion(float var1) {
        this.motion = var1;
    }

    public JumpEvent(float motion) {
        this.motion = motion;
    }
}