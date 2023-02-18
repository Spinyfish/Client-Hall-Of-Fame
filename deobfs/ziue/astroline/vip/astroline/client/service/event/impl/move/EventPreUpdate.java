/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.move;

import vip.astroline.client.service.event.Event;

public class EventPreUpdate
extends Event {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    private boolean modified;

    public EventPreUpdate(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.y = y;
        this.z = z;
        this.x = x;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.modified = true;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.modified = true;
    }

    public void setRotation(float[] rotation) {
        this.setYaw(rotation[0]);
        this.setPitch(rotation[1]);
        this.modified = true;
    }

    public void setY(double posY) {
        this.y = posY;
    }

    public void setZ(double posZ) {
        this.z = posZ;
    }

    public void setX(double posX) {
        this.x = posX;
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isModified() {
        return this.modified;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isCancelled() {
        return super.isCancelled();
    }
}
