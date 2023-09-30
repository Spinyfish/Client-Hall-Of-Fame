package com.alan.clients.module.impl.movement.speed;

import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;

/**
 * @author Alan
 * @since 18/11/2022
 */

public class MineMenClubSpeed extends Mode<Speed> {

    public MineMenClubSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        if (mc.thePlayer.hurtTime <= 6) {
            MoveUtil.strafe();
        }
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    };
}