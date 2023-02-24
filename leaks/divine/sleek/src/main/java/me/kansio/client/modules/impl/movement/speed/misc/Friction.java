package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;

public class Friction extends SpeedMode {

    public Friction() {
        super("Friction Abuse");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround) {
            getSpeed().getHDist().set(getSpeed().getHDist().get() + getSpeed().getSpeed().getValue());
        }

        if (mc.thePlayer.isCollidedHorizontally) {
            getSpeed().getHDist().set(0);
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.isMovingOnGround()) {
            event.setMotionY(mc.thePlayer.motionY = PlayerUtil.getMotion(0.42f));
        }

        PlayerUtil.setMotion(getSpeed().handleFriction(getSpeed().getHDist()));
    }
}
