package me.r.touchgrass.ui.ingame.components;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventRender2D;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.settings.Setting;
import net.minecraft.client.Minecraft;

/**
 * Created by r on 18/02/2021
 */
@Info(name = "Watermark", description = "Shows the client name", category = Category.Hud)
public class Watermark extends Module {

    public Watermark() {
        addSetting(new Setting("Background", this, false));
        addSetting(new Setting("Outline", this, false));
        addSetting(new Setting("Time", this, true));
    }

    @EventTarget
    public void drawWatermark(EventRender2D e) {
        if(touchgrass.getClient().panic || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }
        if (touchgrass.getClient().moduleManager.getModulebyName("HUD").isEnabled()) {
            touchgrass.getClient().hud.style.drawWatermark();
        }
    }
}