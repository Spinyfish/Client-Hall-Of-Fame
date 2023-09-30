package felix.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import felix.command.Command;
import felix.command.impl.*;

public class CommandManager {
	
	private HashMap<Class, Command> commands = new LinkedHashMap<>();
	
	public CommandManager() {
		commands.put(Bind.class, new Bind());
		commands.put(Teleport.class, new Teleport());
		commands.put(Toggle.class, new Toggle());
		commands.put(Config.class, new Config());
		commands.put(Yanchop.class, new Yanchop());
		commands.put(Help.class, new Help());
	}
	
	public Collection<Command> getComands() {
		return commands.values();
	}
}
