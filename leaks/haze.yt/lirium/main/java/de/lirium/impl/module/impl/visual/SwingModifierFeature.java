package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.AnimationEndEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Swing Modifier", description = "Modify the swing animation", category = ModuleFeature.Category.VISUAL)
public class SwingModifierFeature extends ModuleFeature {

    @Value(name = "Slowness")
    private final SliderSetting<Integer> slowness = new SliderSetting<>(6, 1, 30);

    @EventHandler
    public final Listener<AnimationEndEvent> eventListener = e -> {
        e.speed = this.slowness.getValue();
    };
}
