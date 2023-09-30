package felix.command.impl;

import felix.Client;
import felix.command.Command;
import felix.module.ClientConfig;
import felix.util.other.Logger;

public class Config extends Command {

	public Config() {
		super("config");
	}

	@Override
	public String usage() {
		return "config save/remove <name>, config load <name>";
	}

	@Override
	public void executeCommand(String[] commandArguments) {
		if (commandArguments.length > 2 || commandArguments.length < 1) {
			this.printUsage();
		}
		if (commandArguments.length == 1) {
			if (commandArguments[0].equalsIgnoreCase("list")) {
				Logger.print("Configs:");
				Client.INSTANCE.getConfigManager().getContents()
						.forEach(clientConfig -> Logger.print(clientConfig.getConfigName()));
			}
			if (!commandArguments[0].equalsIgnoreCase("list")) {
				this.printUsage();
			}
		}

		if (commandArguments.length == 2) {
			String arg0 = commandArguments[0];
			String configName = commandArguments[1];

			if (!arg0.equalsIgnoreCase("save") || !arg0.equalsIgnoreCase("load") || !arg0.equalsIgnoreCase("remove")) {
				this.printUsage();
			}
			
			final ClientConfig config = new ClientConfig(configName);
			if (arg0.equalsIgnoreCase("save")) {
				Logger.print("Saved config " + configName);
				Client.INSTANCE.getConfigManager().saveConfig(config);
			} 
			else if (arg0.equalsIgnoreCase("load")) {
				if (!config.getConfigFile().exists()) return;
				Logger.print("Loaded config " + configName);
				Client.INSTANCE.getConfigManager().loadConfig(config);
			}
			if (arg0.equalsIgnoreCase("remove")) {
				if (!config.getConfigFile().exists()) return;
				Client.INSTANCE.getConfigManager().deleteConfig(config);
			}
		}
	}
}
