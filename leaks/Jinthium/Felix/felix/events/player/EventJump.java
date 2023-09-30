package felix.events.player;

import felix.events.Cancellable;

public class EventJump extends Cancellable {
    public double motionY;

    public EventJump(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionY(double d) {
        this.motionY = d;
    }

    public double getMotionY() {
        return motionY;
    }
}
