package me.r.touchgrass.module.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.client.Minecraft;
import me.r.touchgrass.events.EventSafeWalk;
import me.r.touchgrass.utils.ReflectionUtil;

/**
 * Created by r on 07/02/2021
 */

@Info(name = "SafeWalk", description = "Doesn't let you fall off of blocks", category = Category.Movement)
public class SafeWalk extends Module {

    public boolean safewalk;

    public SafeWalk() {}

    @Override
    public void onDisable() {
        safewalk = false;
    }

    @EventTarget
    public void onUpdate(EventSafeWalk e) {
        if(this.isEnabled()) {
            try {
                e.setCancelled(!ReflectionUtil.pressed.getBoolean(Minecraft.getMinecraft().gameSettings.keyBindJump) && mc.thePlayer.onGround);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
