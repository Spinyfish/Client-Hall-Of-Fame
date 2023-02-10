/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 10.09.22, 09:07
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;

import java.awt.*;

@Feature.Info(name = "Glow", category = Feature.Category.VISUAL)
public class GlowFeature extends Feature {

    public final ListSetting<ModuleAddons> modules = new ListSetting<>("Modules", ModuleAddons.TARGETHUD);

    public final NumberSetting glowRadius = new NumberSetting("Glow Radius", 5, 1, 20, 1);
    public final ColorSetting glowColor = new ColorSetting("Glow Color", Color.CYAN);

    public enum ModuleAddons {
        ARRAYLIST("ArrayList"),
        TARGETHUD("TargetHUD"),
        SESSIONINFO("Session Info");

        private final String name;

        ModuleAddons(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
