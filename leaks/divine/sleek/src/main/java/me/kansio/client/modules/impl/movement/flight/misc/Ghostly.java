package me.kansio.client.modules.impl.movement.flight.misc;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;

public class Ghostly extends FlightMode {
    public Ghostly() {
        super("Ghostly");
    }

    @Override
    public void onEnable() {
        PlayerUtil.damageVerus();
    }

    @Override
    public void onMove(MoveEvent event) {
        double motionY = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            motionY = getFlight().getSpeed().getValue() / 2;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            motionY = -(getFlight().getSpeed().getValue() / 2);
        }

        mc.thePlayer.motionY = -0.000000000121;


        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * getFlight().getSpeed().getValue();
        double z = Math.cos(yaw) * getFlight().getSpeed().getValue();

        if (!mc.thePlayer.isMoving()) return;

        if (mc.thePlayer.ticksExisted % 5 == 0) {
            mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
        }
    }
}
