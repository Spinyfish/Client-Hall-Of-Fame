/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventStrafing
extends Event {
    @NonNull
    private float yaw;
    @NonNull
    private float pitch;

    public EventStrafing(@NonNull float yaw, @NonNull float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @NonNull
    public float getYaw() {
        return this.yaw;
    }

    @NonNull
    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(@NonNull float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(@NonNull float pitch) {
        this.pitch = pitch;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventStrafing)) {
            return false;
        }
        EventStrafing other = (EventStrafing)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        return Float.compare(this.getPitch(), other.getPitch()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventStrafing;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        return result;
    }

    public String toString() {
        return "EventStrafing(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }
}

