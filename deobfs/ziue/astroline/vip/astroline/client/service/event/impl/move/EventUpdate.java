/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.move;

import net.minecraft.client.Minecraft;
import vip.astroline.client.service.event.Event;

public class EventUpdate
extends Event {
    public boolean onGround;
    public float yaw;
    public float pitch;
    public double y;
    public boolean pre;

    public EventUpdate(float yaw, float pitch, double y, boolean onGround, boolean pre) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
        this.onGround = onGround;
        this.pre = pre;
    }

    public EventUpdate(float yaw, float pitch, double y, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
        this.onGround = onGround;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getY() {
        return this.y;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setYaw(float yaw) {
        Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRotations(float[] rotations) {
        this.setYaw(rotations[0]);
        this.setPitch(rotations[1]);
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isPre() {
        return this.pre;
    }
}
