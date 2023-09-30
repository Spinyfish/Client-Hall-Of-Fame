package io.github.raze.modules.collection.visual;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
public class FullBright extends BaseModule {
    private int gammabefore;

    public FullBright() {
        super("FullBright", "See in the darkness", ModuleCategory.VISUAL);
    }

    public void onEnable() {
        gammabefore = (int) mc.gameSettings.gammaSetting;
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        mc.gameSettings.gammaSetting = 5;
    }

    public void onDisable() {
        mc.gameSettings.gammaSetting = gammabefore;
    }

}
