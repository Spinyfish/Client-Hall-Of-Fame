package com.alan.clients.script.api.wrapper.impl.event.impl;


import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.script.api.wrapper.impl.event.CancellableScriptEvent;

/**
 * @author Auth
 * @since 10/07/2022
 */
public class ScriptStrafeEvent extends CancellableScriptEvent<StrafeEvent> {

    public ScriptStrafeEvent(final StrafeEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setForward(final float forward) {
        this.wrapped.setForward(forward);
    }

    public void setStrafe(final float strafe) {
        this.wrapped.setStrafe(strafe);
    }

    public void setFriction(final float friction) {
        this.wrapped.setFriction(friction);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public float getForward() {
        return this.wrapped.getForward();
    }

    public float getStrafe() {
        return this.wrapped.getStrafe();
    }

    public float getFriction() {
        return this.wrapped.getFriction();
    }

    public float getYaw() {
        return this.wrapped.getYaw();
    }

    public void setSpeed(final double speed, final double motionMultiplier) {
        this.wrapped.setSpeed(speed, motionMultiplier);
    }

    public void setSpeed(final double speed) {
        this.wrapped.setSpeed(speed);
    }

    @Override
    public String getHandlerName() {
        return "onStrafe";
    }
}
