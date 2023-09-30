package dev.tenacity.event.impl.player.movement;

import dev.tenacity.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude(Strategy.NAME_REMAPPING)
public class MotionEvent extends Event.StateEvent {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getX() {
        return x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setX(double x) {
        this.x = x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getY() {
        return y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setY(double y) {
        this.y = y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getZ() {
        return z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setZ(double z) {
        this.z = z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getYaw() {
        return yaw;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public float getPitch() {
        return pitch;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isOnGround() {
        return onGround;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setRotations(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
