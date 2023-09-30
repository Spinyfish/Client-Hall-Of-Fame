package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.longs.LongJumpMode;
import dev.tenacity.module.settings.impl.ModeSetting;

public final class LongJump extends Module {

    public static final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Verus");
    private LongJumpMode jump;

    public LongJump() {
        super("LongJump", Category.MOVEMENT, "Allows you to jump further.");

        this.addSettings(mode);
        LongJumpMode.init();
    }

    @Override
    public void onEnable() {
        this.jump = LongJumpMode.get(mode.getMode());
        this.jump.onEnable();
        Tenacity.INSTANCE.getEventProtocol().register(this.jump);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.jump = LongJumpMode.get(mode.getMode());
        this.jump.onDisable();
        Tenacity.INSTANCE.getEventProtocol().unregister(this.jump);
        super.onDisable();
    }
}
