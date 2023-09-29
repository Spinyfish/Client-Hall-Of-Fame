package de.verschwiegener.atero.command.commands;


import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;

import java.util.Arrays;

public class SkidIrcCommand extends Command {

	public SkidIrcCommand() {
		super("irc", "Skid irc commands", null);
	}

	@Override
	public void onCommand(String[] args) {
		Management.instance.ircClient.executeCommand(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
	}
}
