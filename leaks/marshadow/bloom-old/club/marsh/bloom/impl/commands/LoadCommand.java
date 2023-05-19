package club.marsh.bloom.impl.commands;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.config.ConfigManager;
import marshscript.ScriptManager;

public class LoadCommand extends Command {

	public LoadCommand() {
		super("load");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		if (args.length == 0)
			ConfigManager.loadModules();
		Bloom.INSTANCE.scriptManager.reload(ConfigManager.scriptDirectory);
	}


}
