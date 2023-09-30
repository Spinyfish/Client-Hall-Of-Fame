package dev.tenacity.commands.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.KeybindSetting;
import org.lwjgl.input.Keyboard;

public class ClearBindsCommand extends Command {

    public ClearBindsCommand() {
        super("clearbinds", "Clears all of your keybinds", ".clearbinds");
    }

    @Override
    public void execute(String[] args) {
        int count = 0;
        for (Module module : Tenacity.INSTANCE.getModuleCollection().getModules()) {
            KeybindSetting keybind = module.getKeybind();
            if (keybind.getCode() != Keyboard.KEY_NONE) {
                keybind.setCode(Keyboard.KEY_NONE);
                count++;
            }
        }
        sendChatWithPrefix("Binds cleared! " + count + " modules affected.");
    }

}
