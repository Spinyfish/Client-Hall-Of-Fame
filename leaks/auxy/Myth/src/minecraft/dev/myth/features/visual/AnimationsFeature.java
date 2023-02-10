/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 18:53
 */
package dev.myth.features.visual;

import dev.myth.api.feature.Feature;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;

@Feature.Info(
        name = "Animations",
        description = "Change Blockhit animation",
        category = Feature.Category.VISUAL
)
public class AnimationsFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final NumberSetting itemScale = new NumberSetting("Item Scale", 1, 0.0, 2.0, 0.1);
    public final NumberSetting itemSlowdown = new NumberSetting("Item Slowdown", 1, -1, 6, 0.1);
    public final NumberSetting itemDistance = new NumberSetting("Item Distance", 1, 1, 2, 0.01);
    public final NumberSetting itemX = new NumberSetting("Item X", 0, -0.3, 0.3, 0.01);
    public final NumberSetting itemY = new NumberSetting("Item Y", 0, -0.3, 0.3, 0.01);
    public final BooleanSetting equipProgress = new BooleanSetting("Equip Progress", false);
    public final NumberSetting equipProgressMultiplier = new NumberSetting("Equip Progress Multiplier", 1, 0, 2, 0.05).addDependency(equipProgress::getValue);

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        VANILLA("Vanilla"),
        OLD("Old"),
        EXHIBITION("Exhibition"),
        ETB("ETB"),
        SWANK("Swank"),
        SWANG("Swang"),
        AUXY("Auxy");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
