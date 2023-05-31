package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.impl.events.PosLookEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "No Rotate", description = "Prevents rotation change", category = ModuleFeature.Category.PLAYER)
public class NoRotateFeature extends ModuleFeature {

    @EventHandler
    public final Listener<PosLookEvent> posLookEventListener = e -> {
        e.yaw = getYaw();
        e.pitch = getPitch();
    };
}
