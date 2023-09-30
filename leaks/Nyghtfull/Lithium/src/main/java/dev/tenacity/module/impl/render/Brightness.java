package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

public final class Brightness extends Module {

    @Override
    public void onMotionEvent(MotionEvent event) {
        mc.gameSettings.gammaSetting = 100;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 0;
        super.onDisable();
    }

    public Brightness() {
        super("Brightness", Category.RENDER, "changes the game brightness");
    }

}
