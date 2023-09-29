package me.r.touchgrass.module.modules.combat;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 26/07/2021
 */

@Info(name = "Reach", description = "Extends your reach", category = Category.Combat)
public class Reach extends Module {

    public Reach() {
        addSetting(new Setting("Distance", this, 4, 3, 7, false));
    }

}
