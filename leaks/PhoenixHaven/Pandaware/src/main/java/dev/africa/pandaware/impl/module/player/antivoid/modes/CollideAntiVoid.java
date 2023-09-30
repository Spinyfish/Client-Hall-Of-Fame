package dev.africa.pandaware.impl.module.player.antivoid.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.CollisionEvent;
import dev.africa.pandaware.impl.module.player.antivoid.AntiVoidModule;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class CollideAntiVoid extends ModuleMode<AntiVoidModule> {
    @EventHandler
    EventCallback<CollisionEvent> onCollide = event -> {
        if (mc.thePlayer != null && mc.thePlayer.fallDistance >= this.getParent().getFallDistance().getValue().floatValue() && !PlayerUtils.isBlockUnderNoCollisions()) {
            if (!Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled()) {
                PlayerUtils.setCollisionGround(event);
            }
        }
    };

    public CollideAntiVoid(String name, AntiVoidModule parent) {
        super(name, parent);
    }
}
