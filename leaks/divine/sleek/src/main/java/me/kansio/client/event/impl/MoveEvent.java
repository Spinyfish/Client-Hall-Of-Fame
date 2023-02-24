package me.kansio.client.event.impl;

import me.kansio.client.event.Event;

public class MoveEvent extends Event {

    private double motionX, motionY, motionZ;
    private double strafeSpeed;

    public MoveEvent(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    public void setStrafeSpeed(double strafeSpeed) {
        this.strafeSpeed = strafeSpeed;
    }

    public double getStrafeSpeed() {
        return strafeSpeed;
    }
}