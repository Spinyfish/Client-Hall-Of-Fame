/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.event.impl;

import lodomir.dev.event.Event;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.client.Minecraft;

public class EventStrafe
extends Event {
    private float forward;
    private float strafe;
    private float friction;
    private Minecraft mc = Minecraft.getMinecraft();

    public EventStrafe(float forward, float strafe, float friction) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
    }

    public float getForward() {
        return this.forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getFriction() {
        return this.friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setSpeedPartialStrafe(float friction, float strafe) {
        float remainder = 1.0f - strafe;
        if (this.forward != 0.0f && this.strafe != 0.0f) {
            friction = (float)((double)friction * 0.91);
        }
        if (this.mc.thePlayer.onGround) {
            this.setSpeed(friction);
        } else {
            this.mc.thePlayer.motionX *= (double)strafe;
            this.mc.thePlayer.motionZ *= (double)strafe;
            this.setFriction(friction * remainder);
        }
    }

    public void setSpeed(float speed, double motionMultiplier) {
        this.setFriction(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * 0.99f : speed);
        this.mc.thePlayer.motionX *= motionMultiplier;
        this.mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(float speed) {
        this.setFriction(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * 0.99f : speed);
        MovementUtils.stop();
    }
}

