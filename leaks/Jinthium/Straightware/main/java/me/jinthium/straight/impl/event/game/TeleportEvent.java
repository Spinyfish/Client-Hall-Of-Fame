package me.jinthium.straight.impl.event.game;

import me.jinthium.straight.api.event.Event;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class TeleportEvent extends Event {

    private C03PacketPlayer response;
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;


    public TeleportEvent(C03PacketPlayer response, double posX, double posY, double posZ, float yaw, float pitch) {
        this.response = response;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public C03PacketPlayer getResponse() {
        return response;
    }

    public void setResponse(C03PacketPlayer response) {
        this.response = response;
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