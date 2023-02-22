package com.alan.clients.module.impl.movement.wallclimb;

import com.alan.clients.module.impl.movement.WallClimb;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.value.Mode;

/**
 * @author Nicklas
 * @since 05.06.2022
 */

public class KauriWallClimb extends Mode<WallClimb> {

    public KauriWallClimb(String name, WallClimb parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (InstanceAccess.mc.thePlayer.isCollidedHorizontally) {
            if (InstanceAccess.mc.thePlayer.ticksExisted % 3 == 0) {
                event.setOnGround(true);
                InstanceAccess.mc.thePlayer.jump();
            }
        }
    };
}