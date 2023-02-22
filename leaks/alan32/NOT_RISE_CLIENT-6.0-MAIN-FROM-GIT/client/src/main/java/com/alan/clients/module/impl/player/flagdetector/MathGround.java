package com.alan.clients.module.impl.player.flagdetector;

import com.alan.clients.module.impl.player.FlagDetector;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;


public class MathGround extends Mode<FlagDetector> {

    public MathGround(String name, FlagDetector parent) {
        super(name, parent);
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (event.isOnGround() && event.getPosY() % (1 / 64f) != 0) {
            getParent().fail("Math Ground");
        }

    };
}
