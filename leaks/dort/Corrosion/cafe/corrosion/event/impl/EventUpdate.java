package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventUpdate extends Event {
    private double x;
    private double y;
    private double z;
    private float rotationYaw;
    private float rotationPitch;
    private boolean onGround;
    private final boolean pre;

    public EventUpdate(double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround, boolean pre) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
        this.pre = pre;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isPre() {
        return this.pre;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
