package com.alan.clients.module.impl.player.nofall;

import com.alan.clients.component.impl.player.FallDistanceComponent;
import com.alan.clients.module.impl.player.NoFall;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;

/**
 * @author Alan
 * @since 3/02/2022
 */
public class MatrixNoFall extends Mode<NoFall> {

    public MatrixNoFall(String name, NoFall parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        float distance = FallDistanceComponent.distance;

        if (PlayerUtil.isBlockUnder()) {
            if (distance > 2) {
                MoveUtil.strafe(0.19);
            }

            if (distance > 3 && MoveUtil.speed() < 0.2) {
                event.setOnGround(true);
                distance = 0;
            }
        }

        FallDistanceComponent.distance = distance;
    };
}