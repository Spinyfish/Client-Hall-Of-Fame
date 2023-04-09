package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.PacketEvent;

/**
 * @author Auth
 */

public class InvMove extends Module {

    private final KeyBinding[] keyBindings = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
    };

    public InvMove(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void onMove(PacketEvent event) {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat)
            return;

        for (KeyBinding keyBinding : keyBindings) {
            keyBinding.pressed = Keyboard.isKeyDown(keyBinding.getKeyCode());
        }
    }
}
