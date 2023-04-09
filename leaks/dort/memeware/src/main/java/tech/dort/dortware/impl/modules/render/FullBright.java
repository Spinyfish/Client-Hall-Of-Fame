package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.UpdateEvent;

/**
 * @author Auth
 */

// moved from memeware

public class FullBright extends Module {

    private float lastBrightness;

    public FullBright(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.gammaSetting = 999L;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastBrightness = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 999L;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.gammaSetting = lastBrightness;
    }
}