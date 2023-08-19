package dev.thread.impl.module.movement;

import dev.thread.api.event.bus.annotation.Subscribe;
import dev.thread.api.module.Module;
import dev.thread.api.module.ModuleCategory;
import dev.thread.api.util.render.RenderUtil;
import dev.thread.api.util.render.StencilUtil;
import dev.thread.client.Thread;
import dev.thread.impl.event.Render2DEvent;
import dev.thread.impl.event.UpdateEvent;
import org.lwjglx.input.Keyboard;

import java.awt.*;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Sets the sprinting state", ModuleCategory.MOVEMENT);
        getKey().getValue().setKey(Keyboard.KEY_LCONTROL);
    }

    @Subscribe
    private void onUpdateEvent(UpdateEvent ignored) {
        if (!mc.thePlayer.isSprinting() && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
            mc.thePlayer.setSprinting(true);
        }
    }
}
