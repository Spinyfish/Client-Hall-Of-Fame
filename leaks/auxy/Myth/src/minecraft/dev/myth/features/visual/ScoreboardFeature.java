/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 12.08.22, 00:45
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.NumberSetting;

@Feature.Info(name = "Scoreboard", category = Feature.Category.VISUAL)
public class ScoreboardFeature extends Feature {

    public final BooleanSetting hide = new BooleanSetting("Hide Scoreboard", false);
    public final NumberSetting xPosition = new NumberSetting("X Pos", 1, 0, 1000, 10);
    public final NumberSetting yPosition = new NumberSetting("Y Pos", 1, 0, 2000, 10);
}
