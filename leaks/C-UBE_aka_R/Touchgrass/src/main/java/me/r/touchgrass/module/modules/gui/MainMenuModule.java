package me.r.touchgrass.module.modules.gui;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 26/02/2021
 */
@Info(name = "MainMenu", category = Category.Gui, description = "Enables the custom main menu")
public class MainMenuModule extends Module {
    public MainMenuModule() {
        addSetting(new Setting("Rainbow", this, true));
        addSetting(new Setting("Startup Sound", this, true));
    }
}
