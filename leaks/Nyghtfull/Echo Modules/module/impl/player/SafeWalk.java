package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.SafeWalkEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public final class SafeWalk extends Module {
    @Link
    public Listener<SafeWalkEvent> motionEventListener = e -> {
        if(mc.thePlayer == null) return;
        e.setSafe(true);
    };

    public SafeWalk() {
        super("Safe Walk", Category.PLAYER, "prevents walking off blocks");
    }

}
