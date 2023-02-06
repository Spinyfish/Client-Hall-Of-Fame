package dev.elysium.client.command;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.impl.*;
import dev.elysium.client.events.EventChat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CommandManager {
	public List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";
	
	public void setup()
	{
		commands.add(new Toggle());
		commands.add(new Bind());
		commands.add(new Help());
		commands.add(new Panic());
		commands.add(new Say());
		commands.add(new VClip());
		commands.add(new Show());
		commands.add(new Config());
		commands.add(new Banstats());
		commands.add(new Debug());
		commands.add(new Script());
		
	}
	
	public CommandManager() {
		setup();
	}
	
	public void handleChat(EventChat event)
	{
		if(!event.isPre())
			return;

		String message = event.getMessage();
		
		if(!message.startsWith(prefix))
		{
			return;
		}
		
		event.setCancelled(true);
		
		message = message.substring(prefix.length());
		
		boolean foundCommand = false;
		
		if(message.split(" ").length > 0)
		{
			String commandName = message.split(" ")[0];
			
			for(Command c : commands)
			{
				if(c == null)
					continue;
				
				if(c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName))
				{
					c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
					foundCommand = true;
					break;
				}
			}
		}
		
		if(!foundCommand)
		{
			Elysium.getInstance().addChatMessage("Could not find command");
		}
	}
}
