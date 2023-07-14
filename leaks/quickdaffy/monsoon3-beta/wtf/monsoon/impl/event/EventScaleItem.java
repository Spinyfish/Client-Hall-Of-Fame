/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import wtf.monsoon.api.event.Event;

public class EventScaleItem
extends Event {
    private float scaleX;
    private float scaleY;
    private float scaleZ;

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public float getScaleZ() {
        return this.scaleZ;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventScaleItem)) {
            return false;
        }
        EventScaleItem other = (EventScaleItem)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getScaleX(), other.getScaleX()) != 0) {
            return false;
        }
        if (Float.compare(this.getScaleY(), other.getScaleY()) != 0) {
            return false;
        }
        return Float.compare(this.getScaleZ(), other.getScaleZ()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventScaleItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getScaleX());
        result = result * 59 + Float.floatToIntBits(this.getScaleY());
        result = result * 59 + Float.floatToIntBits(this.getScaleZ());
        return result;
    }

    public String toString() {
        return "EventScaleItem(scaleX=" + this.getScaleX() + ", scaleY=" + this.getScaleY() + ", scaleZ=" + this.getScaleZ() + ")";
    }

    public EventScaleItem(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }
}

