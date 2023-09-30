package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.MoveEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class VanillaSpeed extends SpeedMode {

    public VanillaSpeed() {
        super("Vanilla");
    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        MovementUtils.setSpeed(event,1);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (mc.thePlayer.onGround)
            mc.thePlayer.jump();

        super.onMotionEvent(event);
    }
}
