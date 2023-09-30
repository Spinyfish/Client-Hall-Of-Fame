package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PlayerSizeEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Player Resizer", description = "Change the player size", category = ModuleFeature.Category.VISUAL)
public class PlayerResizerFeature extends ModuleFeature {

    @Value(name = "Size")
    private final SliderSetting<Float> size = new SliderSetting<>(0.9375F, 0.01F, 2F);

    @EventHandler
    public final Listener<PlayerSizeEvent> sizeEventListener = e -> {
        e.size = size.getValue();
    };
}
