/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 10.09.22, 09:06
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;

@Feature.Info(name = "Blur", category = Feature.Category.VISUAL)
public class BlurFeature extends Feature {

    public final ListSetting<ModuleAddons> modules = new ListSetting<>("Modules", ModuleAddons.TARGETHUD);
    public final NumberSetting blurRadius = new NumberSetting("Blur Radius", 5, 1, 25, 1);
    public final NumberSetting blurSigma = new NumberSetting("Blur Sigma", 5, 1, 25, 1);

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
