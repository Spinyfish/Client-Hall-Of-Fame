package cafe.corrosion.command.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.player.PlayerUtil;

@CommandAttributes(
    name = "toggle"
)
public class ToggleCommand implements ICommand {
    private static final String ERROR_MESSAGE = "Invalid syntax! Try -toggle (module name)";

    public void handle(String[] args) {
        if (args.length != 1) {
            PlayerUtil.sendMessage("Invalid syntax! Try -toggle (module name)");
        } else {
            String moduleName = args[0];
            Module module = Corrosion.INSTANCE.getModuleManager().getModule(moduleName);
            String toggleState = module.isEnabled() ? "&cdisabled" : "&aenabled";
            module.toggle();
            PlayerUtil.sendMessage("Successfully " + toggleState + " &7" + moduleName.toLowerCase() + "!");
        }
    }
}
