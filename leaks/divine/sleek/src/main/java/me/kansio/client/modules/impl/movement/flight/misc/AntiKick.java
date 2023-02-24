package me.kansio.client.modules.impl.movement.flight.misc;

import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.player.PlayerUtil;

public class AntiKick extends FlightMode {
    public AntiKick() {
        super("AntiKick");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        double motionY = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            motionY = getFlight().getSpeed().getValue() / 2;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            motionY = -(getFlight().getSpeed().getValue() / 2);
        } else {
            if (mc.thePlayer.ticksExisted % 20 == 0) {

                motionY = 2;
            }
            else {
                motionY = -0.1;
            }
        }

        mc.thePlayer.motionY = motionY;
        PlayerUtil.setMotion(getFlight().getSpeed().getValue());
    }
}
