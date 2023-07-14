/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventMove
extends Event {
    @NonNull
    private double x;
    @NonNull
    private double y;
    @NonNull
    private double z;

    public EventMove(@NonNull double x, @NonNull double y, @NonNull double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void setX(@NonNull double x) {
        this.x = x;
    }

    public void setY(@NonNull double y) {
        this.y = y;
    }

    public void setZ(@NonNull double z) {
        this.z = z;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventMove)) {
            return false;
        }
        EventMove other = (EventMove)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Double.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        return Double.compare(this.getZ(), other.getZ()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventMove;
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
        return result;
    }

    public String toString() {
        return "EventMove(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }
}

