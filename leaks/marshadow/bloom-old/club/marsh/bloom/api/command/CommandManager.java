package club.marsh.bloom.api.command;

import java.util.concurrent.CopyOnWriteArrayList;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.commands.*;

public class CommandManager {
	public static CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<Command>();
	public CommandManager() {
		Bloom.INSTANCE.eventBus.register(this);
		commands.add(new BindCommand());
		commands.add(new HelpCommand());
		commands.add(new SaveCommand());
		commands.add(new LoadCommand());
		commands.add(new BindsCommand());
		commands.add(new SayCommand());
		commands.add(new ToggleCommand());
		commands.add(new FriendCommand());
	}
}
