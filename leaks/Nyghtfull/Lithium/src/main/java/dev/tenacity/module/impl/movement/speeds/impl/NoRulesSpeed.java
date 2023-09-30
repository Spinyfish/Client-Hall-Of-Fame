package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class NoRulesSpeed extends SpeedMode {

    public NoRulesSpeed() {
        super("NoRules");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }

        mc.timer.timerSpeed = 1.25f;

        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(1f);
            mc.thePlayer.jump();
        }


        MovementUtils.strafe(0.60f);

        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {

        mc.timer.timerSpeed = 1.0F;

        super.onDisable();
    }
}
