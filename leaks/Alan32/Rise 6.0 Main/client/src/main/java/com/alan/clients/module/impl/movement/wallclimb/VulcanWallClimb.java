package com.alan.clients.module.impl.movement.wallclimb;

import com.alan.clients.module.impl.movement.WallClimb;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.util.MathHelper;

/**
 * @author Alan
 * @since 22/3/2022
 */

public class VulcanWallClimb extends Mode<WallClimb> {

    public VulcanWallClimb(String name, WallClimb parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (InstanceAccess.mc.thePlayer.isCollidedHorizontally) {
            if (InstanceAccess.mc.thePlayer.ticksExisted % 2 == 0) {
                event.setOnGround(true);
                InstanceAccess.mc.thePlayer.motionY = MoveUtil.jumpMotion();
            }

            final double yaw = MoveUtil.direction();
            event.setPosX(event.getPosX() - -MathHelper.sin((float) yaw) * 0.1f);
            event.setPosZ(event.getPosZ() - MathHelper.cos((float) yaw) * 0.1f);
        }
    };
}