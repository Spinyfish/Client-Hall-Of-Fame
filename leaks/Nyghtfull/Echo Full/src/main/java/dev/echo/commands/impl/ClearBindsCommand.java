package dev.echo.commands.impl;

import dev.echo.Echo;
import dev.echo.commands.Command;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.KeybindSetting;
import org.lwjgl.input.Keyboard;

public class ClearBindsCommand extends Command {

    public ClearBindsCommand() {
        super("clearbinds", "Clears all of your keybinds", ".clearbinds");
    }

    @Override
    public void execute(String[] args) {
        int count = 0;
        for (Module module : Echo.INSTANCE.getModuleCollection().getModules()) {
            KeybindSetting keybind = module.getKeybind();
            if (keybind.getCode() != Keyboard.KEY_NONE) {
                keybind.setCode(Keyboard.KEY_NONE);
                count++;
            }
        }
        sendChatWithPrefix("Binds cleared! " + count + " modules affected.");
    }

}
