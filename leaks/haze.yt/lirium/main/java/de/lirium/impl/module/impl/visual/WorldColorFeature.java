package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.LightMapEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "World Color", description = "Colorize your world", category = ModuleFeature.Category.VISUAL)
public class WorldColorFeature extends ModuleFeature {

    //Todo: use color picker
    @Value(name = "Red")
    private final SliderSetting<Float> red = new SliderSetting<>(1F, 0F, 1F);

    @Value(name = "Green")
    private final SliderSetting<Float> green = new SliderSetting<>(1F, 0F, 1F);

    @Value(name = "Blue")
    private final SliderSetting<Float> blue = new SliderSetting<>(1F, 0F, 1F);

    @EventHandler
    private final Listener<LightMapEvent> lightMapEventListener = e -> {
        e.overwrite = true;
        e.red = red.getValue();
        e.green = green.getValue();
        e.blue = blue.getValue();
    };
}
