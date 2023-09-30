package haptic.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import haptic.Haptic;
import haptic.command.impl.Bind;
import haptic.command.impl.Config;
import haptic.command.impl.Say;
import haptic.command.impl.Toggle;
import haptic.event.impl.EventChat;

public class CommandManager {
	
	public List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";
	
	public CommandManager() {
		setup();
	}
	
	public void setup() {
		commands.add(new Toggle());
		commands.add(new Say());
		commands.add(new Config());
		commands.add(new Bind());
	}

	public void handleChat(EventChat e) {
		String message = e.getMessage();
		
		if(!message.startsWith(prefix)) {
			return;
		}
		
		e.setCancelled(true);
		
		message = message.substring(prefix.length());
		
		boolean foundCommand = false;
		
		if(message.split(" ").length > 0) {
			String commandName = message.split(" ")[0];
			
			for(Command c : commands) {
				if(c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
					c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
					foundCommand = true;
					break;
				}
			}
		}
		
		if(!foundCommand) {
			Haptic.addChatMessage("Error : Could not find command.");
		}
		
	}
	
}
