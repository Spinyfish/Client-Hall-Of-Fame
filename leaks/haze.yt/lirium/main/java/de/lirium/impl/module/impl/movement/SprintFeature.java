package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.feature.BlinkUtil;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleFeature.Info(name = "Sprint", description = "Sprint automatically", category = ModuleFeature.Category.MOVEMENT)
public class SprintFeature extends ModuleFeature {

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        mc.gameSettings.keyBindSprint.pressed = true;
    };
}