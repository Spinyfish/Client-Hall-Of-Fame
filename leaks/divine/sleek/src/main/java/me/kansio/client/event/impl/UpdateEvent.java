package me.kansio.client.event.impl;

import me.kansio.client.event.Event;
import net.minecraft.client.Minecraft;

public class UpdateEvent extends Event {

    private double posX, posY, posZ;
    private float rotationYaw, rotationPitch;
    private boolean onGround;
    private final boolean isPre;

    public UpdateEvent(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, boolean onGround) {
        this(true);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
    }

    public UpdateEvent(boolean isPre) {
        this.isPre = isPre;
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

    public float getRotationYaw() {
        return rotationYaw;
    }

    public void setRotationYaw(float rotationYaw) {
        Minecraft.getMinecraft().thePlayer.renderYawOffset = rotationYaw;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = rotationYaw;
        this.rotationYaw = rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public void setRotationPitch(float rotationPitch) {
        Minecraft.getMinecraft().thePlayer.renderPitchHead = rotationPitch;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isPre() {
        return isPre;
    }
}
