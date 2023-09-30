package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.potion.Potion;

public class StrafeSpeed extends SpeedMode {

    public StrafeSpeed() {
        super("Strafe");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }
        MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
        if(mc.thePlayer.hurtTime>1) {
            MovementUtils.strafe(0.7f);
        }



        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }

        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;

        super.onDisable();
    }
}
