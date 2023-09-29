package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.play.client.*;

import java.util.UUID;

@ModuleFeature.Info(name = "Debug", description = "LOL how you found this?", category = ModuleFeature.Category.MISCELLANEOUS, wip = true)
public class DebugFeature extends ModuleFeature {

    @EventHandler
    public Listener<UpdateEvent> updateEventListener = e -> {

    };

    @EventHandler
    public Listener<PacketEvent> packetEventListener = e -> {

    };
}
