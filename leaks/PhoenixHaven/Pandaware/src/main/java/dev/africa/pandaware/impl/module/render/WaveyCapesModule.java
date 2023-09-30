package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.event.TaskedEventListener;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Wavey Capes", category = Category.VISUAL)
public class WaveyCapesModule extends Module {
    private final NumberSetting gravity = new NumberSetting("Gravity", 50, 1, 24, 1);
    private final NumberSetting heightMultiplier = new NumberSetting("Height multiplier",
            50, 1, 10, 1);

    private final BooleanSetting wind = new BooleanSetting("Wind", true);
    private final BooleanSetting movementRotation = new BooleanSetting("Movement rotation", false);

    public WaveyCapesModule() {
        this.registerSettings(
                this.gravity,
                this.heightMultiplier,
                this.wind,
                this.movementRotation
        );

        this.setTaskedEvent(new TaskedEventListener<WaveyCapesModule>("Wavey Task", this) {

            @EventHandler
            EventCallback<TickEvent> onTick = event -> UISettings.WAVEY_CAPES = this.getModule().getData().isEnabled();
        });
    }
}