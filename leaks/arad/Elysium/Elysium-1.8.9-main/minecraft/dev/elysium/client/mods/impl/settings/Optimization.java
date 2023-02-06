package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ColorSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;

public class Optimization extends Mod {
    public ColorSetting testColors = new ColorSetting("Colors yay", 0xff00aaff, this);
    public NumberSetting test1 = new NumberSetting("Test numbers", 0, 10, 5, 0.25, this);
    public BooleanSetting smoothCircles = new BooleanSetting("Smooth circles long name xd", true, this);
    public BooleanSetting smoothCircles2 = new BooleanSetting("Arad is", false, this);
    public NumberSetting test2 = new NumberSetting("Test numbers 2 xd", 0, 10, 5, 0.1, this);

    public Optimization() {
        super("Optimization","So u dont have 5fps with intel pentium HD graphics", Category.SETTINGS);
    }
}