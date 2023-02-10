/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 10.09.22, 09:07
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ListSetting;

@Feature.Info(name = "Shadow", category = Feature.Category.VISUAL)
public class ShadowFeature extends Feature {

    public final ListSetting<ModuleAddons> modules = new ListSetting<>("Modules", ModuleAddons.TARGETHUD);

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
