package me.jinthium.straight.impl.event.movement;

import me.jinthium.straight.api.event.Event;

public class JumpFixEvent extends Event {
    private float yaw;

    public JumpFixEvent(float yaw){
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
