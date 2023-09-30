package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.OverlayEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Fire Modifier", description = "Modify the rendering of fire", category = ModuleFeature.Category.VISUAL)
public class FireModifierFeature extends ModuleFeature {

    @Value(name = "Render")
    private final CheckBox render = new CheckBox(true);

    @Value(name = "Alpha")
    private final SliderSetting<Float> alpha = new SliderSetting<>(0.9F, 0.0F, 1.0F, new Dependency<>(render, true));

    @Value(name = "Height")
    private final SliderSetting<Float> height = new SliderSetting<>(1.0F, -2.0F, 2.0F, new Dependency<>(render, true));

    @Value(name = "Size")
    private final SliderSetting<Float> size = new SliderSetting<>(1.0F, 0.0F, 2.0F, new Dependency<>(render, true));

    @Value(name = "Rotation")
    private final SliderSetting<Float> rotation = new SliderSetting<>(0.0F, 0.0F, 360.0F, new Dependency<>(render, true));

    @EventHandler
    public final Listener<OverlayEvent> overlayEventListener = e -> {
        e.renderFire = render.getValue();
        e.fireAlpha = alpha.getValue();
        e.fireHeight = height.getValue();
        e.fireSize = size.getValue();
        e.fireRotation = rotation.getValue();
    };
}
