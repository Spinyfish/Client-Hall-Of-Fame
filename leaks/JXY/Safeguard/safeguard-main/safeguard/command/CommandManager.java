package intentions.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import intentions.Client;
import intentions.command.impl.*;
import intentions.events.listeners.EventChat;
import intentions.modules.render.TabGUI;

public class CommandManager {

	public List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";
	
	public static boolean dontDoCommands = false;
	
	public CommandManager() {
		setup();
	}
	
	public void setup() {
		commands.add(new Toggle());
		commands.add(new Config());
		commands.add(new VClip());
		commands.add(new Null());
		commands.add(new Bind());
		commands.add(new Say());
	}
	
	
	public void handleChat(EventChat event) {
		String message = event.getMessage();
		
		if(!message.startsWith(prefix) || !TabGUI.openTabGUI || dontDoCommands)
			return;
		
		event.setCancelled(true);
		
		message = message.substring(prefix.length());
		
		if (message.split(" ").length > 0) {
			String commandName = message.split(" ")[0];
			
			for(Command c : commands) {
				if(c.aliases.contains(commandName.toLowerCase()) || c.name.toLowerCase().equalsIgnoreCase(commandName.toLowerCase())) {
					c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
					return;
				}
			}
			if(commandName.length() > 0) {
				Client.addChatMessage("Unknown command: " + commandName);
			} else
				Client.addChatMessage("Cannot run 'null' sowwy xD");
		}else
			Client.addChatMessage("Cannot run 'null' sowwy xD");
		
		
	}
}
