package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Particle Timer", description = "Changes the speed of particles", category = ModuleFeature.Category.VISUAL)
public class ParticleTimerFeature extends ModuleFeature {

    @Value(name = "Speed")
    final SliderSetting<Float> speed = new SliderSetting<>(1F, 0.01F, 2F);

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(String.valueOf(speed.getValue()));
        mc.particleTimer.timerSpeed = speed.getValue();
    };

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.particleTimer.timerSpeed = 1F;
        super.onDisable();
    }
}
