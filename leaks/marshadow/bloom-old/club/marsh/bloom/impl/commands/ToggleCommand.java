package club.marsh.bloom.impl.commands;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.module.Module;

public class ToggleCommand extends Command {

	public ToggleCommand() {
		super("toggle");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		if (!arg.isEmpty() && !arg.startsWith(" ")) {
			boolean foundMod = false;
            for (Module mod : Bloom.INSTANCE.moduleManager.getModules()) {
                if (mod.getName().toLowerCase().contains(arg.toLowerCase())) {
                    mod.setToggled(!mod.isToggled());
                    addMessage(mod.getName() + " has been toggled!");
                    foundMod = true;
                    break;
                }
            }
            
            if (!foundMod)
            {
            	addMessage("Mod not found!");
            }
        } else {
            addMessage(".toggle [mod]");
        }
	}
}
