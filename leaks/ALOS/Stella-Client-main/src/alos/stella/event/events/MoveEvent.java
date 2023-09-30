package alos.stella.event.events;

import kotlin.jvm.JvmName;
import alos.stella.event.CancellableEvent;

public final class MoveEvent extends CancellableEvent {
    private boolean isSafeWalk;
    private double x;
    private double y;
    private double z;

    public final boolean isSafeWalk() {
        return this.isSafeWalk;
    }

    public final void setSafeWalk(boolean var1) {
        this.isSafeWalk = var1;
    }

    public final void zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public final void zeroXZ() {
        this.x = 0.0;
        this.z = 0.0;
    }

    @JvmName(
            name = "setX1"
    )
    public final void setX1(double x) {
        this.x = x;
    }

    @JvmName(
            name = "setZ1"
    )
    public final void setZ1(double z) {
        this.z = z;
    }

    public final double getX() {
        return this.x;
    }

    public final void setX(double var1) {
        this.x = var1;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double var1) {
        this.y = var1;
    }

    public final double getZ() {
        return this.z;
    }

    public final void setZ(double var1) {
        this.z = var1;
    }

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
