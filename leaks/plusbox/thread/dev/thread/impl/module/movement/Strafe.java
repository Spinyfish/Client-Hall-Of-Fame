package dev.thread.impl.module.movement;

import dev.thread.api.event.bus.annotation.Subscribe;
import dev.thread.api.module.Module;
import dev.thread.api.module.ModuleCategory;
import dev.thread.api.util.player.MoveUtil;
import dev.thread.impl.event.UpdateEvent;
import org.lwjglx.input.Keyboard;

public class Strafe extends Module {
    public Strafe() {
        super("Strafe", "Automatically strafes.", ModuleCategory.MOVEMENT);
        getKey().getValue().setKey(Keyboard.KEY_X);
    }

    @Subscribe
    private void onUpdateEvent(UpdateEvent ignored) {
        MoveUtil.strafe();
    }
}
