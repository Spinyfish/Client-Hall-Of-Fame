package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;
import me.kansio.client.utils.player.TimerUtil;


public class TimerHop extends SpeedMode {

    public TimerHop() {
        super("TimerHop");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
            mc.thePlayer.speedInAir = 0.0204f;
            TimerUtil.setTimer(0.65f);
            mc.gameSettings.keyBindJump.pressed = true;
        } else {
            TimerUtil.Reset();
            mc.gameSettings.keyBindJump.pressed = false;
        }

        if (mc.thePlayer.isMoving()) {
            if (mc.thePlayer.fallDistance < 0.1) {
                TimerUtil.setTimer(1.81f);
            }
            if (mc.thePlayer.fallDistance > 0.2) {
                TimerUtil.setTimer(0.42f);
            }
            if (mc.thePlayer.fallDistance > 0.6) {
                TimerUtil.setTimer(1.05f);
                mc.thePlayer.speedInAir = 0.02019f;
            }
        }

        if (mc.thePlayer.fallDistance > 1) {
            TimerUtil.Reset();
            mc.thePlayer.speedInAir = 0.02f;
        }
    }
}
