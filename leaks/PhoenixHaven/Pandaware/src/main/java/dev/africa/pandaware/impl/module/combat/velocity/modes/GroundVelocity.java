package dev.africa.pandaware.impl.module.combat.velocity.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;

public class GroundVelocity extends ModuleMode<VelocityModule> {
    public GroundVelocity(String name, VelocityModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.thePlayer.hurtTime > 0) {
            mc.thePlayer.onGround = true;
        }
    };
}
