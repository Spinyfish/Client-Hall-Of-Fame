package haptic.command.impl;

import haptic.Haptic;
import haptic.command.Command;
import haptic.module.Module;

public class Toggle extends Command {
	
	public Toggle() {
		super("Toggle", "Toggles a module by name", "toggle <name>", "t");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length > 0) {
			String moduleName = args[0];
			
			boolean foundModule = false;
			
			for(Module module : Haptic.getModuleManager().getModules()) {
				if(module.getName().equalsIgnoreCase(moduleName)) {
					module.toggle();
					
					Haptic.addChatMessage((module.isEnabled() ? "Enabled" : "Disabled") + " " + module.getName());
					
					foundModule = true;
					break;
				}
			}
			
			if(!foundModule) {
				Haptic.addChatMessage("Error : Could not find module " + moduleName + ".");
			}
			
		}
	}

}
