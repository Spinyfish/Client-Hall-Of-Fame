package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.setting.NumberSetting;

@ModuleInfo(name = "Fullbright", category = Category.VISUAL)
public class FullbrightModule extends Module {
    private final NumberSetting gammaS = new NumberSetting("Gamma Increase", 100, 1, 100, 1);

    public FullbrightModule() {
        this.registerSettings(this.gammaS);
    }

    private float gamma;

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        mc.gameSettings.gammaSetting = gamma + gammaS.getValue().floatValue();
    };

    @Override
    public void onEnable() {
        if (gamma != -1f) {
            gamma = mc.gameSettings.gammaSetting;
        }
    }

    @Override
    public void onDisable() {
        if (gamma != -1f) {
            mc.gameSettings.gammaSetting = gamma;
        }
    }
}
