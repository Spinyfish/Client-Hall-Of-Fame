package me.kansio.client.modules.impl.movement.flight.bridgerland;

import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * @author Moshi
 * he made this after I told him how epic worked
 */


public class Teleport extends FlightMode {

    public Teleport() {
        super("BridgerLand (TP)");
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (event.isPre()) {
            double motionY = 0;
            double speed = getFlight().getSpeed().getValue();

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                motionY = speed / 5;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                motionY = -speed / 5;
            }

            if (!getFlight().getAntikick().getValue() && !mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0;
            }

            if (mc.thePlayer.ticksExisted % 3 == 0) {
                PlayerUtil.setMotion(speed);
                mc.thePlayer.motionY = motionY;
            } else {
                PlayerUtil.setMotion(0);
            }
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            event.setCancelled(true);
        }
    }
}
