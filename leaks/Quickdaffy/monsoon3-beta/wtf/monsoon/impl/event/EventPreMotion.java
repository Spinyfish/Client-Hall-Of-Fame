/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventPreMotion
extends Event {
    @NonNull
    private double x;
    @NonNull
    private double y;
    @NonNull
    private double z;
    @NonNull
    private float yaw;
    @NonNull
    private float pitch;
    @NonNull
    private boolean onGround;

    public EventPreMotion(@NonNull double x, @NonNull double y, @NonNull double z, @NonNull float yaw, @NonNull float pitch, @NonNull boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @NonNull
    public double getX() {
        return this.x;
    }

    @NonNull
    public double getY() {
        return this.y;
    }

    @NonNull
    public double getZ() {
        return this.z;
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
    public boolean isOnGround() {
        return this.onGround;
    }

    public void setX(@NonNull double x) {
        this.x = x;
    }

    public void setY(@NonNull double y) {
        this.y = y;
    }

    public void setZ(@NonNull double z) {
        this.z = z;
    }

    public void setYaw(@NonNull float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(@NonNull float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(@NonNull boolean onGround) {
        this.onGround = onGround;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventPreMotion)) {
            return false;
        }
        EventPreMotion other = (EventPreMotion)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Double.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        if (Double.compare(this.getZ(), other.getZ()) != 0) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        if (Float.compare(this.getPitch(), other.getPitch()) != 0) {
            return false;
        }
        return this.isOnGround() == other.isOnGround();
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventPreMotion;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $x = Double.doubleToLongBits(this.getX());
        result = result * 59 + (int)($x >>> 32 ^ $x);
        long $y = Double.doubleToLongBits(this.getY());
        result = result * 59 + (int)($y >>> 32 ^ $y);
        long $z = Double.doubleToLongBits(this.getZ());
        result = result * 59 + (int)($z >>> 32 ^ $z);
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        result = result * 59 + (this.isOnGround() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "EventPreMotion(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ")";
    }
}

