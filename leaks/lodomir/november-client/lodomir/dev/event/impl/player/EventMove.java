/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl.player;

import lodomir.dev.event.EventUpdate;

public class EventMove
extends EventUpdate {
    public double x;
    public double y;
    public double z;
    private boolean onGround;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}

