package wtf.automn.events.impl.player;

import wtf.automn.events.events.Event;

public class EventJump implements Event {
    private float yaw;

    public EventJump(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
