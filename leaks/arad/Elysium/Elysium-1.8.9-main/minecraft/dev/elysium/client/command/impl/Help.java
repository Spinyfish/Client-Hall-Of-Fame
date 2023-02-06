package dev.elysium.client.command.impl;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;

public class Help extends Command {
	
	public Help()
	{
		super("Help", "Helps you!", "help", "info");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		for(Command c : Elysium.getInstance().getCmdManager().commands)
		{
			Elysium.getInstance().addChatMessage(c.getName() + ": " + c.getDescription() + " : " + c.getAliases());
		}
	}
	
}
