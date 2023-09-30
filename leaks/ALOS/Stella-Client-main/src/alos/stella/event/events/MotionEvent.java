package alos.stella.event.events;

import alos.stella.event.CancellableEvent;
import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import alos.stella.event.EventState;
import org.jetbrains.annotations.NotNull;

public final class MotionEvent extends CancellableEvent {
    @NotNull
    private EventState eventState;
    private double x;
    private double y;
    private double z;
    public float yaw;
    public float pitch;
    private boolean onGround;

    @NotNull
    public final EventState getEventState() {
        return this.eventState;
    }

    public final void setEventState(@NotNull EventState var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.eventState = var1;
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

    public final float getYaw() {
        return this.yaw;
    }

    public final void setYaw(float var1) {
        this.yaw = var1;
    }

    public final float getPitch() {
        return this.pitch;
    }

    public final void setPitch(float var1) {
        this.pitch = var1;
    }

    public final boolean getOnGround() {
        return this.onGround;
    }

    public final void setOnGround(boolean var1) {
        this.onGround = var1;
    }

    public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.eventState = EventState.PRE;
    }
}
