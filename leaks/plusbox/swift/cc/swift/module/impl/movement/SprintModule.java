/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.module.impl.movement;

import cc.swift.Swift;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.player.ScaffoldModule;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import org.lwjgl.input.Keyboard;

public final class SprintModule extends Module {
    public SprintModule() {
        super("Sprint", Category.MOVEMENT);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {

        ScaffoldModule scaffoldModule = Swift.INSTANCE.getModuleManager().getModule(ScaffoldModule.class);

        if (scaffoldModule.isEnabled()) return;

        mc.gameSettings.keyBindSprint.pressed = true;
    };

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
    }
}
