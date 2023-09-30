package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.ToastsRenderEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Anti Toasts", description = "Disables Achievements being shown at all", category = ModuleFeature.Category.VISUAL)
public class AntiToastsFeature extends ModuleFeature {

    @EventHandler
    public final Listener<ToastsRenderEvent> renderEventListener = e -> {
      e.setCancelled(true);
    };
}
