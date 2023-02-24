package me.kansio.client.modules.impl.movement.speed.misc;

import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;

public class Ghostly extends SpeedMode {
    
    public Ghostly() {
        super("Ghostly");
    }

    @Override
    public void onMove(MoveEvent event) {


        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * getSpeed().getSpeed().getValue();
        double z = Math.cos(yaw) * getSpeed().getSpeed().getValue();

        if (!mc.thePlayer.isMoving()) return;

        if (mc.thePlayer.ticksExisted % 5 == 0) {
            mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
        }


    }
}
