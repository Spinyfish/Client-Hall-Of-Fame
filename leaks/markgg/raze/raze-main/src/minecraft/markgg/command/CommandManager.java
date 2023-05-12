package markgg.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import markgg.Client;
import markgg.command.impl.Bind;
import markgg.command.impl.Config;
import markgg.command.impl.Discord;
import markgg.command.impl.Help;
import markgg.command.impl.IGN;
import markgg.command.impl.Ip;
import markgg.command.impl.Say;
import markgg.command.impl.Toggle;
import markgg.command.impl.VClip;
import markgg.events.listeners.EventChat;

public class CommandManager {

	public List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";

	public CommandManager() {
		setup();
	}
	
	public void setup() {
		commands.add(new Help());
		commands.add(new Toggle());
		commands.add(new Bind());
		commands.add(new VClip());
		commands.add(new Say());
		commands.add(new Config());
		commands.add(new Ip());
		commands.add(new IGN());
		commands.add(new Discord());
	}

	public void handlechat(EventChat event) {
		String message = event.getMessage();
		
		if(!message.startsWith(prefix))
			return;
		
		event.setCancelled(true);
		
		message = message.substring(prefix.length());
		
		boolean foundCommand = false;

		if(message.split(" ").length > 0) {
			String commandName = message.split(" ")[0];
			
			for (Command c : this.commands) {
				if (c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
					c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
					foundCommand = true;
					break;	
				}
			}
		}
		if(!foundCommand) {
			Client.addChatMessage("Could not find command.");
		}
	}

}