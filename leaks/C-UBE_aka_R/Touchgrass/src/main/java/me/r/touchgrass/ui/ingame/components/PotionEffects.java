package me.r.touchgrass.ui.ingame.components;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.events.EventRender2D;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.module.Info;
import net.minecraft.client.Minecraft;

/**
 * Created by r on 15/01/2022
 */
@Info(name = "PotionEffects", description = "Shows active potion effects", category = Category.Hud)
public class PotionEffects extends Module {

    @EventTarget
    public void onRender(EventRender2D e) {
        if(touchgrass.getClient().panic || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }
        if (touchgrass.getClient().moduleManager.getModulebyName("HUD").isEnabled()) {
            touchgrass.getClient().hud.style.drawPotionEffects();
        }
    }

}
