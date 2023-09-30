package club.pulsive.impl.module.impl.misc;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;

@ModuleInfo(name = "Speed Mine", renderName = "Speed Mine", description = "Speed de Mine", category = Category.PLAYER)

public class SpeedMine extends Module {
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if (mc.thePlayer == null) return;
        mc.playerController.blockHitDelay = 0;
        if (mc.playerController.curBlockDamageMP >= 0.7F)
            mc.playerController.curBlockDamageMP = 1.0F;
    };
}
