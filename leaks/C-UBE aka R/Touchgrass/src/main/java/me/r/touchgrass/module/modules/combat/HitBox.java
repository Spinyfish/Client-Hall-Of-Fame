package me.r.touchgrass.module.modules.combat;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 26/07/2021
 */
@Info(name = "HitBox", description = "Extends the enemys hitbox", category = Category.Combat)
public class HitBox extends Module {
    public HitBox() {
        addSetting(new Setting("Expand", this, 0.15, 0.1, 1, false));
    }
}
