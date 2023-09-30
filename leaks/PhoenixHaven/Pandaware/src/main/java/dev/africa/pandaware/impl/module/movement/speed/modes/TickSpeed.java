package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;

public class TickSpeed extends ModuleMode<SpeedModule> {
    private final NumberSetting speed = new NumberSetting("Speed", 10, 0, 1, 0.05);
    private final NumberSetting waitForTicks = new NumberSetting("Wait for ticks", 20, 2, 10, 1);

    public TickSpeed(String name, SpeedModule parent) {
        super(name, parent);

        this.registerSettings(
                this.speed,
                this.waitForTicks
        );
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.ticksExisted % waitForTicks.getValue().intValue() == 0) {
            MovementUtils.strafe(event, this.speed.getValue().floatValue());
        }
    };
}