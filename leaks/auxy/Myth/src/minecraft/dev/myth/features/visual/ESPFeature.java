/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 05.09.22, 00:36
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.ListSetting;

@Feature.Info(name = "ESP", category = Feature.Category.VISUAL)
public class ESPFeature extends Feature {

    public final ListSetting<ESPAddons> espAddon = new ListSetting<>("Addons", ESPAddons.OUTLINE);
    public final ListSetting<Targets> targets = new ListSetting<>("Targets", Targets.ANIMALS);

    public enum ESPAddons {
        OUTLINE("Minecraft Glow"),
        CHAMS("Chams");

        private final String name;

        ESPAddons(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


    public enum Targets {
        Players("Players"),
        ANIMALS("Animals"),
        MOBS("Mobs");

        private final String name;

        Targets(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
