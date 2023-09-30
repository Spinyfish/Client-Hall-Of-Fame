/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import wtf.monsoon.api.event.Event;

public class EventTransformItem
extends Event {
    private float posX;
    private float posY;
    private float posZ;

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public float getPosZ() {
        return this.posZ;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventTransformItem)) {
            return false;
        }
        EventTransformItem other = (EventTransformItem)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getPosX(), other.getPosX()) != 0) {
            return false;
        }
        if (Float.compare(this.getPosY(), other.getPosY()) != 0) {
            return false;
        }
        return Float.compare(this.getPosZ(), other.getPosZ()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventTransformItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getPosX());
        result = result * 59 + Float.floatToIntBits(this.getPosY());
        result = result * 59 + Float.floatToIntBits(this.getPosZ());
        return result;
    }

    public String toString() {
        return "EventTransformItem(posX=" + this.getPosX() + ", posY=" + this.getPosY() + ", posZ=" + this.getPosZ() + ")";
    }

    public EventTransformItem(float posX, float posY, float posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }
}

