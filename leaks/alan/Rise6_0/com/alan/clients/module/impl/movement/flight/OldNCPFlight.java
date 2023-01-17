package com.alan.clients.module.impl.movement.flight;


import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import org.apache.commons.lang3.RandomUtils;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class OldNCPFlight extends Mode<Flight> {

    public OldNCPFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        event.setPosY(event.getPosY() + (mc.thePlayer.ticksExisted % 2 == 0 ? RandomUtils.nextDouble(1E-10, 1E-5) : -RandomUtils.nextDouble(1E-10, 1E-5)));
        mc.thePlayer.motionY = 0;
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        event.setSpeed(MoveUtil.getAllowedHorizontalDistance(), Math.random() / 2000);
    };
}