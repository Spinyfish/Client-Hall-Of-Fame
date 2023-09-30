package dev.africa.pandaware.impl.module.movement.noslow;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.movement.noslow.modes.*;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.Getter;

@Getter
@ModuleInfo(name = "NoSlow", category = Category.MOVEMENT)
public class NoSlowModule extends Module {
    private final NumberSetting multiplier = new NumberSetting("Forward Multiplier", 1, 0.2, 1, 0.01).setSaveConfig(true);
    private final NumberSetting smultiplier = new NumberSetting("Strafe Multiplier", 1, 0.2, 1, 0.01).setSaveConfig(true);

    public NoSlowModule() {
        this.registerModes(
                new NCPNoSlow("NCP", this),
                new VanillaNoSlow("Vanilla", this),
                new HypixelNoSlow("Hypixel", this)
        );

        this.registerSettings(this.multiplier, this.smultiplier);
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
