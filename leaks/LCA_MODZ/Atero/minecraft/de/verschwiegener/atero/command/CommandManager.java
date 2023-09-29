package de.verschwiegener.atero.command;

import java.util.ArrayList;
import java.util.function.Predicate;

import de.verschwiegener.atero.command.commands.*;

import de.verschwiegener.atero.util.chat.ChatUtil;

public class CommandManager {
	
	ArrayList<Command> commands = new ArrayList<>();
	
	public CommandManager() {
		commands.add(new TestCommand());
		commands.add(new BindCommand());
		commands.add(new BindsCommand());
		commands.add(new ConfigCommand());
		commands.add(new GlobalChatCommand());
		commands.add(new SkidIrcCommand());
		commands.add(new LocalChatCommand());
	}
	
	
	public void onCommand(String text) {
	    if (!text.startsWith(".")) {
		return;
	    }

	    String[] args = text.substring(1).split(" ");
	    if (!args[0].isEmpty()) {
		try {
		    getCommandByName(args[0]).onCommand(args);
		} catch (NullPointerException ex) {
		    ChatUtil.sendMessageWithPrefix("Command \"" + args[0] + "\" was not found");
		}
	    }
	}
	
	public Command getCommandByName(final String name) {
		return commands.stream().filter(new Predicate<Command>() {
			@Override
			public boolean test(Command module) {
				return module.getSyntax().toLowerCase().equalsIgnoreCase(name.toLowerCase());
			}
		}).findFirst().orElse(null);
	}
	
	public Command getCommandByStartsWith(final String name) {
		return commands.stream().filter(new Predicate<Command>() {
			@Override
			public boolean test(Command module) {
				return module.getSyntax().toLowerCase().startsWith(name.toLowerCase());
			}
		}).findFirst().orElse(null);
	}

}
