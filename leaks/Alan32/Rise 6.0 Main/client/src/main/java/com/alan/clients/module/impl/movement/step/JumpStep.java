package com.alan.clients.module.impl.movement.step;

import com.alan.clients.module.impl.movement.Step;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;

/**
 * @author Alan
 * @since 22/3/2022
 */

public class JumpStep extends Mode<Step> {

    public JumpStep(String name, Step parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.jump();
        }
    };
}