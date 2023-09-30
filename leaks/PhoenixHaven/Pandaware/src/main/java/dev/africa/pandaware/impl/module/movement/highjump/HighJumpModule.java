package dev.africa.pandaware.impl.module.movement.highjump;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.movement.highjump.modes.*;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.Getter;

@Getter
@ModuleInfo(name = "High Jump", category = Category.MOVEMENT)
public class HighJumpModule extends Module {
    private final NumberSetting height = new NumberSetting("Height", 10, 0.5, 1, 0.1,
            () -> !(this.getCurrentMode() instanceof VerusHighjump));
    private final BooleanSetting onlyOnJump = new BooleanSetting("Only on Key Press", false,
            () -> !(this.getCurrentMode() instanceof VerusHighjump));

    public HighJumpModule() {
        this.registerModes(
                new VanillaHighjump("Vanilla", this),
                new VerusHighjump("Verus", this)
        );

        this.registerSettings(
                this.height,
                this.onlyOnJump
        );
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
