package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;

public class AutoRespawn extends BaseModule {

    public AutoRespawn() {
        super("AutoRespawn", "Respawns you automatically.", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            if (mc.thePlayer.isDead)
                mc.thePlayer.respawnPlayer();
        }
    }
}
