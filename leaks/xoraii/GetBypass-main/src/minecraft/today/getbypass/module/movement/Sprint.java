package today.getbypass.module.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Keyboard.KEY_N, Category.MOVEMENT);
    }

    public void onEnable() {
        if (this.isToggled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            super.onDisable();
        }
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }
}