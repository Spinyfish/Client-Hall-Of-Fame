package wtf.automn.events.impl.player;

import wtf.automn.events.events.Event;

public class EventMoveFlying implements Event {
    private float yaw;
    private float friction;

    public EventMoveFlying(float yaw, float friction) {
        this.yaw = yaw;
        this.friction = friction;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
}
