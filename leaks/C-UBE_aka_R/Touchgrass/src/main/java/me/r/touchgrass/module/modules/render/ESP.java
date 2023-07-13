package me.r.touchgrass.module.modules.render;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 05/02/2021
 */

@Info(name = "ESP", description = "Draws an outline on entities through walls", category = Category.Render)
public class ESP extends Module {
    public ESP() {
        addSetting(new Setting("LineWidth", this, 3, 1, 10, false));
        addSetting(new Setting("Entities", this, true));
    }
}
