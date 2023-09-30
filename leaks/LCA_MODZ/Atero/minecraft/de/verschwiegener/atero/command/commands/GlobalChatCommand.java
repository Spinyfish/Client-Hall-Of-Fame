package de.verschwiegener.atero.command.commands;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;


public class GlobalChatCommand extends Command {

	public GlobalChatCommand() {
		super("c", "Skid IRC global chat", null);
	}

	@Override
	public void onCommand(String[] args) {
		Management.instance.ircClient.sendChatMessage(String.join(" ", args));
	}
}
