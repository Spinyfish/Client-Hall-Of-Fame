package de.fanta.command.impl;

import java.util.Arrays;

import de.fanta.Client;
import de.fanta.command.Command;

public class SkidIrcCommand extends Command {
	public SkidIrcCommand() {
		super("irc");
	}

	@Override
	public void execute(String[] args) {

		Client.INSTANCE.ircClient.executeCommand(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));


	}
}