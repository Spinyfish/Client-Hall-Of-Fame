package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class WatchdogSpeed extends SpeedMode {

    public WatchdogSpeed() {
        super("Watchdog");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }

        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(
                MovementUtils.getBaseMoveSpeed()
            );

            mc.thePlayer.jump();
        }

        super.onMotionEvent(event);
    }
}
