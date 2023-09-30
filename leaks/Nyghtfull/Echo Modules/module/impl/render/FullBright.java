package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;

public final class FullBright extends Module {

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        mc.gameSettings.gammaSetting = 100;
    };

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 0;
        super.onDisable();
    }

    public FullBright() {
        super("FullBright", Category.RENDER, "changes the game brightness");
    }

}
