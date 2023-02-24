package me.kansio.client.modules.impl.movement.speed.verus;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;

public class VerusGround extends SpeedMode {

    public VerusGround() {
        super("Verus Port");
    }

    @Override
    public void onMove(MoveEvent event) {
        if (!mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null) {
            if (mc.thePlayer.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.0;
                    PlayerUtil.strafe(0.61F);
                    event.setMotionY(0.41999998688698);
                }
                PlayerUtil.strafe();
            }
        }
    }
}
