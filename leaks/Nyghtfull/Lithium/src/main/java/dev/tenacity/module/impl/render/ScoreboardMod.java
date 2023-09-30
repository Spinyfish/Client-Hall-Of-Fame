package dev.tenacity.module.impl.render;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;

public class ScoreboardMod extends Module {

    public static final NumberSetting yOffset = new NumberSetting("Y Offset", 0, 250, 1, 5);
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", false);
    public static final BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);
    public static final BooleanSetting redNumbers = new BooleanSetting("Red Numbers", false);

    public ScoreboardMod() {
        super("Scoreboard", Category.RENDER, "Scoreboard preferences");
        this.addSettings(yOffset, customFont, textShadow, redNumbers);
        this.setToggled(true);
    }

}
