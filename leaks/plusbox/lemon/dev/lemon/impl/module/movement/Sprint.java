package dev.lemon.impl.module.movement;

import dev.lemon.api.event.bus.annotation.Subscribe;
import dev.lemon.api.event.bus.listener.IListener;
import dev.lemon.api.module.Module;
import dev.lemon.api.module.ModuleCategory;
import dev.lemon.impl.event.UpdateEvent;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Sets the sprinting state", ModuleCategory.MOVEMENT);
        setKey(Keyboard.KEY_LCONTROL);
    }

    @Subscribe
    private final IListener<UpdateEvent> updateEvent = event -> {
        if (!mc.thePlayer.isSprinting() && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
            mc.thePlayer.setSprinting(true);
        }
    };
}
