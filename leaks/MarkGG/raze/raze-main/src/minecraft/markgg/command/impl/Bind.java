package markgg.command.impl;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.command.Command;
import markgg.config.KeybindSystem;
import markgg.modules.Module;

public class Bind extends Command {

	public Bind() {
		super("Bind", "Binds a module to a key.", "bind <name> <key> | clear", "b");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if (args.length == 2) {
			String moduleName = args[0];
			String keyName = args[1];

			boolean foundModule = false;

			for (Module module : Client.modules) {
				if (module.name.equalsIgnoreCase(moduleName)) {
					module.keyCode.setKeyCode(Keyboard.getKeyIndex(keyName.toUpperCase()));

					Client.addChatMessage(String.format("Bound %s to %s", module.name, Keyboard.getKeyName(module.getKey())));
					KeybindSystem.saveKeybinds("keybinds.json");
					foundModule = true;
					break;
				} 
			} 
			if (!foundModule) {
				Client.addChatMessage("Could not find module.");
			}
		} 

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("clear")) {
				for (Module module : Client.modules) {
					module.keyCode.setKeyCode(Keyboard.KEY_NONE);
					KeybindSystem.saveKeybinds("keybinds.json");
				}
			}

			Client.addChatMessage("Cleared all binds.");
		}
	}

}
