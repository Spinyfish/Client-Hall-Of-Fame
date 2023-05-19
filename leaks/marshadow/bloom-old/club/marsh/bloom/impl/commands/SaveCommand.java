package club.marsh.bloom.impl.commands;

import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.config.ConfigManager;

public class SaveCommand extends Command {

	public SaveCommand() {
		super("save");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		ConfigManager.saveModules();
		addMessage("Saved the config!");
	}
}
