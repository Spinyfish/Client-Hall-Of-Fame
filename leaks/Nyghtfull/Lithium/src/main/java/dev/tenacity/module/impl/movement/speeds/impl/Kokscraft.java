package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;

public class Kokscraft extends SpeedMode {
    int jumps;
    private int offGroundTicks;
    public Kokscraft() {
        super("Kokscraft");
    }




    @Override
    public void onMotionEvent(MotionEvent event) {

        mc.thePlayer.cameraYaw = 0;
        mc.thePlayer.cameraPitch = 0;

        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;

        } else {
            offGroundTicks++;
        }
       if(!mc.thePlayer.onGround) {
           if (mc.thePlayer.hurtTime == 0) MovementUtils.strafe(MovementUtils.getBaseMoveSpeed() * 1.3f);

        //   mc.thePlayer.motionY = 0.1f;
       }
        mc.gameSettings.keyBindSneak.pressed = true;
        if (mc.thePlayer.onGround) {


         //   mc.thePlayer.motionY=0.01f;
            mc.thePlayer.jump();
            jumps++;
        }

       if ( mc.thePlayer.hurtTime == 0) {
           mc.thePlayer.motionY = MovementUtils.predictedMotion(mc.thePlayer.motionY, jumps % 2 == 0 ? 2 : 4);
       }


        super.onMotionEvent(event);
    }

    @Override
    public void onDisable() {
        offGroundTicks = 0;
        mc.timer.timerSpeed = 1.0F;

        super.onDisable();
    }

    @Override
    public void onEnable() {
        jumps = 0;
        mc.timer.timerSpeed = 1.0F;

        super.onEnable();
    }
}
