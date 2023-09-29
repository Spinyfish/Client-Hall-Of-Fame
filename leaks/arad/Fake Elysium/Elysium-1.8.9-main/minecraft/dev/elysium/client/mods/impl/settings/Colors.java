package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;

public class Colors extends Mod {
    public ModeSetting rainbowmode = new ModeSetting("Rainbow",this,"Astolfo","Rainbow","Custom","Christmas");
    public NumberSetting customcolour = new NumberSetting("Custom Index",1, 100, 50, 1, this);
    public NumberSetting custommultiplier = new NumberSetting("Custom Multiplier",1, 6, 2, 0.5, this);

    public Colors() {
        super("Colors","Modify your color scheme", Category.SETTINGS);
    }
}
