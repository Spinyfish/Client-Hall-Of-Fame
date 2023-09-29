package haptic.command.impl;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.command.Command;
import haptic.module.Module;

import java.util.Locale;

public class Bind extends Command {

	public Bind() {
		super("Bind", "Binds a module by name", "bind <name> <key> | clear", "b");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length == 2) {
			String moduleName = args[0];
			String keyName = args[1];

			boolean foundModule = false;

			for(Module module : Haptic.getModuleManager().getModules()) {
				if(module.getName().equalsIgnoreCase(moduleName)) {
					module.getKeybind().setKey(Keyboard.getKeyIndex(keyName.toUpperCase()));

					Haptic.addChatMessage(String.format("Bound %s to %s", module.getName(), module.getKeybind().getKeyName()));
					foundModule = true;
					break;
				}
			}

			if(!foundModule) {
				Haptic.addChatMessage("Could not find module.");
			}
			
		}

		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("clear")) {
				for(Module module : Haptic.getModuleManager().getModules()) {
					module.getKeybind().setKey(Keyboard.KEY_NONE);
				}
				Haptic.addChatMessage("Cleared all binds.");
			}
		}
	}

}
