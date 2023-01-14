package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventJump extends Event {
    private float yaw;
    private float motion;

    public EventJump(float yaw, float motion) {
        this.yaw = yaw;
        this.motion = motion;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getMotion() {
        return this.motion;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setMotion(float motion) {
        this.motion = motion;
    }
}
