package alos.stella.event.events;

import alos.stella.event.CancellableEvent;

import static alos.stella.utils.MinecraftInstance.mc;
import static alos.stella.utils.MovementUtils.getDirection;
import static alos.stella.utils.MovementUtils.isMoving;

public final class StrafeEvent extends CancellableEvent  {
    private float strafe;
    private float forward;
    private float friction;

    public StrafeEvent() {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }

    public final float getStrafe() {
        return this.strafe;
    }

    public final float getForward() {
        return this.forward;
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

    public final float getFriction() {
        return this.friction;
    }

    public StrafeEvent(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
    public void setSpeed(final double speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

}
