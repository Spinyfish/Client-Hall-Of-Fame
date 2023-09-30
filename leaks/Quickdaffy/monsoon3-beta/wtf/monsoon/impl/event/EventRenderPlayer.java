/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventRenderPlayer
extends Event {
    @NonNull
    private float yaw;
    @NonNull
    private float pitch;
    @NonNull
    private float partialTicks;

    public EventRenderPlayer(@NonNull float yaw, @NonNull float pitch, @NonNull float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }

    @NonNull
    public float getYaw() {
        return this.yaw;
    }

    @NonNull
    public float getPitch() {
        return this.pitch;
    }

    @NonNull
    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setYaw(@NonNull float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(@NonNull float pitch) {
        this.pitch = pitch;
    }

    public void setPartialTicks(@NonNull float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRenderPlayer)) {
            return false;
        }
        EventRenderPlayer other = (EventRenderPlayer)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        if (Float.compare(this.getPitch(), other.getPitch()) != 0) {
            return false;
        }
        return Float.compare(this.getPartialTicks(), other.getPartialTicks()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRenderPlayer;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        result = result * 59 + Float.floatToIntBits(this.getPartialTicks());
        return result;
    }

    public String toString() {
        return "EventRenderPlayer(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", partialTicks=" + this.getPartialTicks() + ")";
    }
}

