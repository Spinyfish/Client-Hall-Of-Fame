package io.github.raze.commands.collection;

import io.github.raze.Raze;
import io.github.raze.commands.system.BaseCommand;
import io.github.raze.modules.system.BaseModule;
import org.lwjgl.input.Keyboard;

public class BindCommand extends BaseCommand {

    public BindCommand() {
        super("Bind", "Bind modules to keys", "bind <Module> <Key>", "b");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length < 3) {
            return "Usage: " + getSyntax();
        }

        BaseModule module = Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(arguments[1]);
        int keyCode = Keyboard.getKeyIndex(arguments[2].toUpperCase());

        if (module == null) {
            return String.format("Could not find module %s", arguments[1]);
        }

        module.setKeyCode(keyCode);

        return String.format("Successfully bound %s to the key %s.", module.getName(), Keyboard.getKeyName(keyCode));
    }

}
