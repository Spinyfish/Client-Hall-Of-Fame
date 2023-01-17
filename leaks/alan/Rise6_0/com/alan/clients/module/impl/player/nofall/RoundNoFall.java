package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.util.player.MoveUtil;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class RoundNoFall extends Mode<NoFall> {

    public RoundNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        final double rounded = MoveUtil.roundToGround(event.getPosY());
        final float distance = FallDistanceComponent.distance;

        if (distance > 3 && Math.abs(rounded - event.getPosY()) < 0.005) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, rounded, mc.thePlayer.posZ);
            event.setOnGround(true);
            event.setPosY(rounded);
        }
    };
}