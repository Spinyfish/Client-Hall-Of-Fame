package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.MoveEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import org.lwjgl.input.Keyboard;

@ModuleFeature.Info(name = "Air Jump", description = "Jump in the air", category = ModuleFeature.Category.MOVEMENT)
public class AirJumpFeature extends ModuleFeature {

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        getPlayer().onGround = true;
    };
}