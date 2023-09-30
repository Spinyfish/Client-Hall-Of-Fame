package me.r.touchgrass.ui.ingame.components;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventRender2D;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.settings.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * Created by r on 18/02/2021
 */
@me.r.touchgrass.module.Info(name = "Info", description = "Shows FPS and Coordinates", category = Category.Hud)
public class Info extends Module {

    public Info() {
        ArrayList<String> alignment = new ArrayList<>();
        alignment.add("Left");
        alignment.add("Right");

        ArrayList<String> c_alignment = new ArrayList<>();
        c_alignment.add("1-Line");
        c_alignment.add("3-Line");

        addSetting(new Setting("Alignment", this, "Right", alignment));
        addSetting(new Setting("XYZ Style", this, "1-Line", c_alignment));
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        if(touchgrass.getClient().panic || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }
        if (touchgrass.getClient().moduleManager.getModulebyName("HUD").isEnabled() && !touchgrass.getClient().moduleManager.getModulebyName("Hotbar").isEnabled()) {
            touchgrass.getClient().hud.style.drawInfo();
        }
    }
}
