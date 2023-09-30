package com.darkmagician6.eventapi.events.callables;

import com.darkmagician6.eventapi.events.Event;

import net.minecraft.client.Minecraft;

public class PlayerMoveEvent implements Event {
    private boolean canceled;
    public double x;
    public double y;
    public double z;

    public PlayerMoveEvent(double x, double y, double z) {
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

    public  void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
