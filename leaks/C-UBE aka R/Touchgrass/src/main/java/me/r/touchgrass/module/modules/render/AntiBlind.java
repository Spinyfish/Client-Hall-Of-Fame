package me.r.touchgrass.module.modules.render;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 25/12/2021
 */
@Info(name = "AntiBlind", description = "Removes any visually impairing effects", category = Category.Render)
public class AntiBlind extends Module {

    public AntiBlind() {
        addSetting(new Setting("Pumpkin", this, true));
        addSetting(new Setting("Fire", this, true));
        addSetting(new Setting("Potion", this, true));
    }


}
