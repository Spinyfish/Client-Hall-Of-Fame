package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.player.movement.SafeWalkEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

public final class SafeWalk extends Module {
    @Override
    public void onSafeWalkEvent(SafeWalkEvent e) {
        if(mc.thePlayer == null) return;
        e.setSafe(true);
    }
    public SafeWalk() {
        super("SafeWalk", Category.PLAYER, "prevents walking off blocks");
    }

}
