package me.r.touchgrass.module.modules.render;

import com.darkmagician6.eventapi.EventManager;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;

@Info(name = "Fullbright", description = "Brightens up the world", category = Category.Render)
public class Fullbright extends Module {
    public Fullbright() {}

    @Override
    public void onEnable() {
        mc.gameSettings.gammaSetting = 5f;
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 0f;
        EventManager.unregister(this);
    }


}
