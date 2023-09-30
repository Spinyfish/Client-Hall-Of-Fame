package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import markgg.modules.Module;

public class Help extends Command {

	public Help() {
		super("Help", "Shows basic helpful information", "help", "h");
	}

	@Override
	public void onCommand(String[] args, String command) {
		Client.addChatMessage();
		Client.addChatMessage("Version " + Client.version);
		Client.addChatMessage();
		Client.addChatMessage(".bind <module> <key> - Binds a module to a key.");
		Client.addChatMessage(".bind clear - Clears all binds.");
		Client.addChatMessage(".toggle <module> - Toggles a specific module.");
		Client.addChatMessage(".vclip <blocks> - Clip up or down.");
		Client.addChatMessage(".config save/load <name> - Saves or loads a config.");
		Client.addChatMessage(".config list - Lists all the available configs.");
		Client.addChatMessage(".say - Says things in chat");
		Client.addChatMessage(".ip - Shows the servers ip");
		Client.addChatMessage(".ign - Shows your ingame name");
		Client.addChatMessage(".discord - Shows our discord invite link");
		Client.addChatMessage();
	}

}
