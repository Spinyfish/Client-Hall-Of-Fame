package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class SpoofNoFall extends Mode<NoFall> {

    public SpoofNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        float distance = FallDistanceComponent.distance;

        if (distance > 3) {
            event.setOnGround(true);
            distance = 0;
        }

        FallDistanceComponent.distance = distance;
    };
}