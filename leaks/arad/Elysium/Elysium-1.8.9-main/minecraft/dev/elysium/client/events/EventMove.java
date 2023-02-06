package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;

public class EventMove extends Event {
    boolean safewalk;
    double x,y,z;

    public boolean isSafewalk() {
        return safewalk;
    }

    public void setSafewalk(boolean safewalk) {
        this.safewalk = safewalk;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public EventMove(double x, double y, double z, boolean safewalk) {
        this.safewalk = safewalk;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
