package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.speeds.SpeedMode;
import dev.tenacity.module.settings.impl.ModeSetting;

@SuppressWarnings("unused")
public final class Speed extends Module {

    private SpeedMode mode;
    private final ModeSetting selection = new ModeSetting("Mode", "Latest NCP","Watchdog 2","Kokscraft","Polar", "Strafe", "Vanilla", "NoRules", "Watchdog", "Latest NCP");

    public Speed() {
        super("Speed", Category.MOVEMENT, "Makes you go faster");
        this.addSettings(selection);
        SpeedMode.init();
    }

    @Override
    public void onEnable() {
        this.mode = SpeedMode.get(selection.getMode());
        this.mode.onEnable();

        Tenacity.INSTANCE.getEventProtocol().register(this.mode);
        this.setSuffix(this.mode.getName());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.mode.onDisable();
        Tenacity.INSTANCE.getEventProtocol().unregister(this.mode);
        super.onDisable();
    }
}