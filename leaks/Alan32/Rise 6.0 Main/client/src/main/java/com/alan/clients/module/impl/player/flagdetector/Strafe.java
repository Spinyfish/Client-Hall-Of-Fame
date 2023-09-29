package com.alan.clients.module.impl.player.flagdetector;

import com.alan.clients.module.impl.player.FlagDetector;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;


public class Strafe extends Mode<FlagDetector> {

    public Strafe(String name, FlagDetector parent) {
        super(name, parent);
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.offGroundTicks <= 1 || event.isOnGround() || mc.thePlayer.ticksSinceVelocity == 1 ||
                mc.thePlayer.capabilities.isFlying || mc.thePlayer.isCollidedHorizontally ||
                mc.thePlayer.ticksSinceTeleport == 1 || mc.thePlayer.ticksSincePlayerVelocity <= 20) return;

        double moveFlying = 0.02599999835384377;
        double friction = 0.9100000262260437;

        double lastDeltaX = Math.abs(mc.thePlayer.lastMotionX) * friction;
        double lastDeltaZ = Math.abs(mc.thePlayer.lastMotionZ) * friction;

        double deltaX = Math.abs(mc.thePlayer.motionX);
        double deltaZ = Math.abs(mc.thePlayer.motionZ);

        if (Math.abs(lastDeltaX - deltaX) > moveFlying || Math.abs(lastDeltaZ - deltaZ) > moveFlying) {
            getParent().fail("Strafe");
        }

    };
}
