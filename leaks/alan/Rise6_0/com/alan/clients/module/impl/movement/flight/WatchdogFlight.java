package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Alan
 * @since 11/06/2022
 */

public class WatchdogFlight extends Mode<Flight> {
    public WatchdogFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
            switch (mc.thePlayer.offGroundTicks) {
                case 0:
                    mc.thePlayer.jump();
                    MoveUtil.strafe(9 - Math.random() / 100f);
                    break;

                default:
//                    if (mc.thePlayer.fallDistance < 1) {
//                        MoveUtil.strafe(MoveUtil.speed() * 1.026);
//                        if (mc.thePlayer.fallDistance > 0) mc.thePlayer.motionY += 0.02;
//                    }
                    break;
            }
        }

//        mc.timer.timerSpeed = 0.5f;

    };

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }
}