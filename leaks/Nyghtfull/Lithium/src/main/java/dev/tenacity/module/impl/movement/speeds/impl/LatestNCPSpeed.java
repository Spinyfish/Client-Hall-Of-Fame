package dev.tenacity.module.impl.movement.speeds.impl;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;

public class LatestNCPSpeed extends SpeedMode {

    public LatestNCPSpeed() {
        super("Latest NCP");

    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C0BPacketEntityAction) {
            event.cancel();
        }
    }


    @Override
    public void onMotionEvent(MotionEvent event) {

        if (event.isPost() || !MovementUtils.isMoving() || MovementUtils.isInLiquid()) {
            return;
        }

        if(mc.thePlayer.hurtTime >1) {
            MovementUtils.strafe(
                    MovementUtils.getSpeed() * 1.2F
            );
        }

        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(
                    MovementUtils.getBaseMoveSpeed()
            );

            mc.thePlayer.jump();

            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                MovementUtils.strafe(
                        MovementUtils.getSpeed() * 1.2F
                );
            }
        }

        mc.timer.timerSpeed = (float) (1.075F - (Math.random() - 0.5) / 100.0F);


        MovementUtils.strafe(
                MovementUtils.getSpeed() - (float) (Math.random() - 0.5F) / 100.0F
        );

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
