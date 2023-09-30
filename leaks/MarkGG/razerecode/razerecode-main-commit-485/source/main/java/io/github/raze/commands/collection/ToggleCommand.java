package io.github.raze.commands.collection;

import io.github.raze.Raze;
import io.github.raze.commands.system.BaseCommand;
import io.github.raze.modules.system.BaseModule;

public class ToggleCommand extends BaseCommand {

    public ToggleCommand() {
        super("Toggle", "Toggles the modules state", "toggle <Module>", "t");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        BaseModule module = Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(arguments[1]);

        if (module == null) {
            return String.format("Could not find module %s", arguments[1]);
        }

        module.toggle();

        return String.format("Toggled %s.", module.getName());
    }

}
