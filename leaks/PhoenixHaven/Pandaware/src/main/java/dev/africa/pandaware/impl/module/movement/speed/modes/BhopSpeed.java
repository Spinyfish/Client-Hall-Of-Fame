package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;

public class BhopSpeed extends ModuleMode<SpeedModule> {
    private final NumberSetting speed = new NumberSetting("Speed", 10, 0, 0.5, 0.1);

    public BhopSpeed(String name, SpeedModule parent) {
        super(name, parent);

        this.registerSettings(this.speed);
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            event.y = mc.thePlayer.motionY = 0.42F;
        }

        MovementUtils.strafe(event, this.speed.getValue().floatValue());
    };
}
