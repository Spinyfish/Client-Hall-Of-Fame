package io.github.raze.modules.collection.combat;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;

public class NoClickDelay extends BaseModule {

    public NoClickDelay() {
        super("NoClickDelay", "Removes hit delay.", ModuleCategory.COMBAT);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        mc.leftClickCounter = 0;
    }

}
