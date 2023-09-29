package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;

public class EventJump extends Event {
    public boolean sprinting;
    public float yaw;

    public EventJump(float yaw, boolean sprinting) {
        this.yaw = yaw;
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
