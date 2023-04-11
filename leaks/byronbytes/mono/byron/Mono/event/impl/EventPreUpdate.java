package byron.Mono.event.impl;

import byron.Mono.event.Event;


public class EventPreUpdate extends Event { // This one is 100% implomented.

    private float[] rotation;
    private boolean onGround;

    public EventPreUpdate(float[] rotation, boolean onGround) {
        this.rotation = rotation;
        this.onGround = onGround;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

}
