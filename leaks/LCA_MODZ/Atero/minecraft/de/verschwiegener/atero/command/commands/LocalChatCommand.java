package de.verschwiegener.atero.command.commands;


import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;

public class LocalChatCommand extends Command {

	public LocalChatCommand() {
		super("l", "Skid IRC local chat",null);
	}

	@Override
	public void onCommand(String[] args) {
		Management.instance.ircClient.sendLocalChatMessage(String.join(" ", args));
	}
}
