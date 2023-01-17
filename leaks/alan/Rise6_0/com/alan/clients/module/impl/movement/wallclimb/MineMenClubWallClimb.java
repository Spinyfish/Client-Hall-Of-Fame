package com.alan.clients.module.impl.movement.wallclimb;

import com.alan.clients.module.impl.movement.WallClimb;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;

/**
 * @author Alan
 * @since 05.06.2022
 */

public class MineMenClubWallClimb extends Mode<WallClimb> {

    private boolean hitHead;

    public MineMenClubWallClimb(String name, WallClimb parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (InstanceAccess.mc.thePlayer.isCollidedHorizontally && !hitHead && InstanceAccess.mc.thePlayer.ticksExisted % 3 == 0) {
            InstanceAccess.mc.thePlayer.motionY = MoveUtil.jumpMotion();
        }

        if (InstanceAccess.mc.thePlayer.isCollidedVertically) {
            hitHead = !InstanceAccess.mc.thePlayer.onGround;
        }
    };
}