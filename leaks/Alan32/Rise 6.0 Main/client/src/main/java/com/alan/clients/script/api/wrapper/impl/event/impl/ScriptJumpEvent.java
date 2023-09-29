package com.alan.clients.script.api.wrapper.impl.event.impl;


import com.alan.clients.newevent.impl.motion.JumpEvent;
import com.alan.clients.script.api.wrapper.impl.event.CancellableScriptEvent;

/**
 * @author Auth
 * @since 9/07/2022
 */
public class ScriptJumpEvent extends CancellableScriptEvent<JumpEvent> {

    public ScriptJumpEvent(final JumpEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public void setJumpMotion(final float jumpMotion) {
        this.wrapped.setJumpMotion(jumpMotion);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public float getJumpMotion() {
        return this.wrapped.getJumpMotion();
    }

    public float getYaw() {
        return this.wrapped.getYaw();
    }

    @Override
    public String getHandlerName() {
        return "onJump";
    }
}
