package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.potion.Potion;

public class CubecraftSpeed extends ModuleMode<SpeedModule> {
    public CubecraftSpeed(String name, SpeedModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (MovementUtils.isMoving()) {
            double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.2) : 0);

            mc.gameSettings.keyBindJump.pressed = false;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                event.y = mc.thePlayer.motionY = 0.42f;
                MovementUtils.strafe(event, 0.6 + speedAmplifier);
            } else {
                if (mc.thePlayer.getAirTicks() == 2 && !mc.thePlayer.isCollidedHorizontally && !(mc.thePlayer.hurtTime > 0)
                        && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    mc.thePlayer.motionY = -0.07;
                } else if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    if (mc.thePlayer.getAirTicks() == 3) {
                        mc.thePlayer.motionY -= 0.07;
                    }

                    if (mc.thePlayer.getAirTicks() == 4) {
                        mc.thePlayer.motionY += 0.01;
                    }
                }
            }
            MovementUtils.strafe(MovementUtils.getSpeed());
        }
    };
}
