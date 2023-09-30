package alos.stella.module.modules.movement;

import alos.stella.event.EventTarget;
import alos.stella.event.events.JumpEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.MovementUtils;
import alos.stella.value.FloatValue;
import net.minecraft.potion.Potion;

@ModuleInfo(name = "LegitSpeed",spacedName = "Legit Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class LegitSpeed extends Module {

    @EventTarget
    public void onJump(JumpEvent event) {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() == 1) {
                MovementUtils.strafe(0.43f);
            }
        }
    }
}
