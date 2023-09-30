package dev.elysium.client.command.impl;

import dev.elysium.client.command.Command;

public class Debug extends Command {
	
	public static boolean debug = false;
	
	public Debug()
	{
		super("Debug", "DEVELOPMENT ONLY DELETE WHEN RELEASED", "d", "d"); //TODO: remove
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		debug = true;
	}
}
