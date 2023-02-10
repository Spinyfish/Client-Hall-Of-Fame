/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 26.10.22, 17:01
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;

import java.awt.*;

@Feature.Info(name = "DamageColor", category = Feature.Category.VISUAL)
public class DamageColorFeature extends Feature {

    public final EnumSetting<TrailsFeature.ColorMode> colorMode = new EnumSetting<>("Color", TrailsFeature.ColorMode.SYNC);
    public final ColorSetting customColor = new ColorSetting("Custom Color", Color.CYAN).addDependency(() -> colorMode.is(TrailsFeature.ColorMode.CUSTOM));
    public final NumberSetting rainbowDelay = new NumberSetting("Color Delay", 200, 50, 500, 1).addDependency(() -> colorMode.is(TrailsFeature.ColorMode.RAINBOW));
    public final NumberSetting alpha = new NumberSetting("Alpha", 100, 30, 255, 1);

    public Color getColor() {
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
        Color color = null;

        switch (colorMode.getValue()) {
            case SYNC:
                color = hudFeature.arrayListColor.getValue();
                break;
            case CUSTOM:
                color = customColor.getValue();
                break;
            case RAINBOW:
                color = ColorUtil.rainbow(rainbowDelay.getValue().intValue());
                break;
        }
        return color;
    }
}
