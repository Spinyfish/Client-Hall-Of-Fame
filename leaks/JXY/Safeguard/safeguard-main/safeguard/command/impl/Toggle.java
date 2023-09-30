package intentions.command.impl;

import intentions.Client;
import intentions.command.Command;
import intentions.modules.Module;

public class Toggle extends Command {

	public Toggle() {
		super("Toggle", "Toggle modules by name", "toggle <name>", new String[] {"t", "toggle"});
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length > 0) {
			String moduleName = args[0];
			
			if (args[0].toLowerCase().equals("tabgui")) {
				Client.addChatMessage("Could not find module " + args[0]);
			} else {
				boolean foundModule = false;
				
				for(Module module : Client.modules) {
					if(module.name.equalsIgnoreCase(moduleName)) {
						module.toggle();
							
						foundModule = true;
						break;
					}
				}
					
				if (!foundModule) {
					Client.addChatMessage("Could not find module " + args[0]);
				}
			}
		} else {
			Client.addChatMessage("Enter module to toggle");
		}
	}
}
