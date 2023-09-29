package me.r.touchgrass.module.modules.player;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 16/01/2022
 */
@Info(name = "NoSwing", description = "Cancels the swing animation", category = Category.Render)
public class NoSwing extends Module {

    public NoSwing() {
        addSetting(new Setting("Server-side", this, false));
    }

}
