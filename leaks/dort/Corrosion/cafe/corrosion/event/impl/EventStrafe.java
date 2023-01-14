package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class EventStrafe extends Event {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private float strafe;
    private float forward;
    private float friction;
    private float yaw;

    public void setMotion(double speed) {
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
        speed *= this.strafe != 0.0F && this.forward != 0.0F ? 0.91D : 1.0D;
        this.setFriction((float)speed);
    }

    public void setMotionLegit(float friction) {
        this.setFriction(mc.thePlayer.onGround ? friction : friction * 0.43F);
    }

    public void setMotionPartialStrafe(float friction, float strafeComponent) {
        float remainder = 1.0F - strafeComponent;
        if (this.forward != 0.0F && this.strafe != 0.0F) {
            friction = (float)((double)friction * 0.91D);
        }

        if (mc.thePlayer.onGround) {
            this.setMotion((double)friction);
        } else {
            EntityPlayerSP var10000 = mc.thePlayer;
            var10000.motionX *= (double)strafeComponent;
            var10000 = mc.thePlayer;
            var10000.motionZ *= (double)strafeComponent;
            this.setFriction(friction * remainder);
        }

    }

    public EventStrafe(float strafe, float forward, float friction, float yaw) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
