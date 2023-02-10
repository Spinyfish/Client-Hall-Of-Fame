/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 13.09.22, 15:13
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ColorSetting;

import java.awt.*;

@Feature.Info(name = "WorldColor", category = Feature.Category.VISUAL)
public class WorldColorFeature extends Feature {

    public final ColorSetting colorSetting = new ColorSetting("Color", Color.CYAN);

}
