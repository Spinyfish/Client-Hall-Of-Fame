package dev.africa.pandaware.impl.module.player.nofall.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.player.nofall.NoFallModule;

public class GroundNoFall extends ModuleMode<NoFallModule> {
    public GroundNoFall(String name, NoFallModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.getParent().canFall() && mc.thePlayer.fallDistance > 2 && mc.thePlayer.ticksExisted % 2 == 0) {
            event.setOnGround(true);
        }
    };
}
