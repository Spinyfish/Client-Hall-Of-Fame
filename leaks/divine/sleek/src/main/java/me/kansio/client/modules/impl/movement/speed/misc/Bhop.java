package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.player.PlayerUtil;

public class Bhop extends SpeedMode {

    public Bhop() {
        super("Bhop");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround) {
            getSpeed().getHDist().set(getSpeed().getSpeed().getValue());
        }
        if (mc.thePlayer.isMovingOnGround()) {
            mc.thePlayer.motionY = PlayerUtil.getMotion(0.42f);
        }
        PlayerUtil.setMotion(getSpeed().handleFriction(getSpeed().getHDist()));
    }
}
