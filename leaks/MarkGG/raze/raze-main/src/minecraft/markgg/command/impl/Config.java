package markgg.command.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import markgg.Client;
import markgg.command.Command;
import markgg.config.ConfigSystem;
import markgg.modules.Module;

public class Config extends Command {

	public Config() {
		super("Config", "Saves or loads configs", "config load <name> | config save | config list", "cfg");
	}

	@Override
	public void onCommand(String[] args, String command) {
		String filename;
		if (args.length > 1 && !args[1].isEmpty()) {
			filename = args[1];
			if (!filename.endsWith(".json")) {
				filename += ".json";
			}
		} else {
			filename = "default.json";
		}
		
		File folder = new File("Raze/configs");
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

		try {
			if(args[0].equalsIgnoreCase("save")) {
				Client.cfgSystem.saveConfig(filename);
				Client.addChatMessage("Saved config to §a" + filename);
			}
			if(args[0].equalsIgnoreCase("load")) {
				Client.print("error: " + Client.cfgSystem.error);
				Client.cfgSystem.loadConfig(filename);
				if(Client.cfgSystem.error != "no error") {
					Client.addChatMessage("Error loading config: §cincompatible version");
					return;
				}
				Client.addChatMessage("Successfully loaded config §a" + filename);
			}
			if(args[0].equalsIgnoreCase("list")) {
				if (files != null && files.length > 0) {
				    StringBuilder messageBuilder = new StringBuilder("Available configs: §a");

				    for (File file : files) {
				        String fileName = file.getName();
				        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				        messageBuilder.append(fileName).append(", ");
				    }

				    String message = messageBuilder.toString().trim();
				    if (message.endsWith(",")) {
				        message = message.substring(0, message.length() - 1);
				    }

				    Client.addChatMessage(message);
				} else {
					Client.addChatMessage("No configs found.");
				}
			}
		} catch (Exception e) {
			Client.addChatMessage("Error getting config: " + e.getMessage());
		    e.printStackTrace();
		}
	}

}
