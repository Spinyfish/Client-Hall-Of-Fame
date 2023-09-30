package io.github.raze.modules.collection.visual;

import io.github.raze.Raze;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.NumberSetting;

public class Animations extends BaseModule {

    public final ArraySetting mode, swing;
    public final NumberSetting slowdown, scale, xPos, yPos;

    public Animations() {
        super("Animations", "Custom sword animations", ModuleCategory.VISUAL);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Sigma", "Sigma", "Exhibition", "Exhibition2", "1.7", "Astolfo"),
                swing = new ArraySetting(this, "Swing Style", "Normal", "Normal", "Smooth"),
                slowdown = new NumberSetting(this, "Slowdown", 0.1, 3.5, 1.2),
                scale = new NumberSetting(this, "Scale", 0.01, 1, 0.4),
                xPos = new NumberSetting(this, "X", 0.10, 1, 0.56),
                yPos = new NumberSetting(this, "Y", 0.10, 1, 0.52)
        );
    }

}
