package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import markgg.modules.Module;

public class Toggle extends Command {

	public Toggle() {
		super("Toggle", "Toggles a specific module.", "toggle <name>", "t");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if (args.length > 0) {
			String moduleName = args[0];

			boolean foundModule = false;

			for (Module module : Client.modules) {
				if (module.name.equalsIgnoreCase(moduleName)) {
					module.toggle();
					
					Client.addChatMessage((module.isEnabled() ? "Enabled" : "Disabled") + " " + module.name);
					
					foundModule = true;
					break;
				} 
			} 
			if (!foundModule)
				Client.addChatMessage("Could not find module."); 
		} 
	}

}
