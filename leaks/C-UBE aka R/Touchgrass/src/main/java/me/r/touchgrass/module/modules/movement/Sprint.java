package me.r.touchgrass.module.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import me.r.touchgrass.events.EventUpdate;

@Info(name = "Sprint", description = "Automatically sprints when W is pressed", category = Category.Movement)
public class Sprint extends Module {
    public Sprint() {}

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.setSprinting(true);
        }
    }
}
