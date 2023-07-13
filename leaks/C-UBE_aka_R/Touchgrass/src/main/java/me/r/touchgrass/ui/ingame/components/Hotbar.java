package me.r.touchgrass.ui.ingame.components;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.events.EventRender2D;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by r on 18/02/2021
 */
@Info(name = "Hotbar", category = Category.Hud, description = "Shows an advanced hotbar with information")
public class Hotbar extends Module {

    public Hotbar() {
        addSetting(new Setting("FPS", this, true));
        addSetting(new Setting("Coordinates", this, true));
        addSetting(new Setting("Time / Date", this, true));
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        if(touchgrass.getClient().panic || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        if (touchgrass.getClient().moduleManager.getModulebyName("HUD").isEnabled()) {
            touchgrass.getClient().hud.style.drawHotbar();
        }
    }
}
