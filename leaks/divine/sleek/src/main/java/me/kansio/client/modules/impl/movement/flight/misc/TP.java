package me.kansio.client.modules.impl.movement.flight.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;

public class TP extends FlightMode {
    public TP() {
        super("TP");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.motionY = 0;

        if (mc.thePlayer.ticksExisted % 5 == 0) {
            double[] negroids = PlayerUtil.teleportForward(getFlight().getSpeed().getValue());
            if (!mc.thePlayer.isMoving()) {
                return;
            }
            mc.thePlayer.setPosition(mc.thePlayer.posX + negroids[0], mc.thePlayer.posY, mc.thePlayer.posZ + negroids[1]);
        }
    }
}
