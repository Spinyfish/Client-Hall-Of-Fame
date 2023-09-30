/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import wtf.monsoon.api.event.Event;

public class EventFOVUpdate
extends Event {
    private final float fov;
    private float newFOV;

    public EventFOVUpdate(float fov) {
        this.fov = fov;
        this.newFOV = fov;
    }

    public float getFov() {
        return this.fov;
    }

    public float getNewFOV() {
        return this.newFOV;
    }

    public void setNewFOV(float newFOV) {
        this.newFOV = newFOV;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventFOVUpdate)) {
            return false;
        }
        EventFOVUpdate other = (EventFOVUpdate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getFov(), other.getFov()) != 0) {
            return false;
        }
        return Float.compare(this.getNewFOV(), other.getNewFOV()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventFOVUpdate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getFov());
        result = result * 59 + Float.floatToIntBits(this.getNewFOV());
        return result;
    }

    public String toString() {
        return "EventFOVUpdate(fov=" + this.getFov() + ", newFOV=" + this.getNewFOV() + ")";
    }
}

