package tech.dort.dortware.impl.events;

import tech.dort.dortware.api.event.Event;

public final class ServerPosLookEvent extends Event {

    private double posX, posY, posZ;
    private float yaw, pitch;
    private float sendYaw, sendPitch;

    public ServerPosLookEvent(double posX, double posY, double posZ, float yaw, float pitch, float sendYaw, float sendPitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.sendYaw = sendYaw;
        this.sendPitch = sendPitch;
    }

    public float getSendYaw() {
        return sendYaw;
    }

    public void setSendYaw(float sendYaw) {
        this.sendYaw = sendYaw;
    }

    public float getSendPitch() {
        return sendPitch;
    }

    public void setSendPitch(float sendPitch) {
        this.sendPitch = sendPitch;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}