package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.MoveEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.rotation.RotationUtil;

@ModuleFeature.Info(name = "Correct Movement", description = "Corrects the player movement", category = ModuleFeature.Category.MOVEMENT)
public class CorrectMovementFeature  extends ModuleFeature {

    @EventHandler
    public final Listener<MoveEvent> moveEventListener = e -> {
        e.yaw = RotationUtil.yaw;
    };
}
