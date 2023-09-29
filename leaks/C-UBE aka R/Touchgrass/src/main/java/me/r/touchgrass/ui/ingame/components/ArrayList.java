package me.r.touchgrass.ui.ingame.components;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventRender2D;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Created by r on 18/02/2021
 */
@Info(name = "ArrayList", description = "Shows enabled modules", category = Category.Hud)
public class ArrayList extends Module {

    public ArrayList() {
        java.util.ArrayList<String> array = new java.util.ArrayList<>();
        array.add("Rainbow");
        array.add("White");
        array.add("Category");
        java.util.ArrayList<String> sort = new java.util.ArrayList<>();
        sort.add("Length");
        sort.add("Alphabetical");

        addSetting(new Setting("Outline", this, true));
        addSetting(new Setting("Background", this, true));
        addSetting(new Setting("List Color",this, "Rainbow", array));
        addSetting(new Setting("Sorting", this, "Length", sort));
        addSetting(new Setting("List Delay", this, 5, 0, 20, true));
        addSetting(new Setting("Rb. Color Count", this, 100, 50, 1000, true));
        addSetting(new Setting("Rb. Saturation", this, 0.4, 0, 1, false));
        addSetting(new Setting("Rb. Delay", this, 4, 1, 10, true));
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        if(touchgrass.getClient().panic || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }
        if (touchgrass.getClient().moduleManager.getModulebyName("HUD").isEnabled()) {
            touchgrass.getClient().hud.style.drawArrayList();
        }
    }
}
