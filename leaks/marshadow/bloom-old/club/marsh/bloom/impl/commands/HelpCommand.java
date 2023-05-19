package club.marsh.bloom.impl.commands;

import club.marsh.bloom.api.command.Command;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		addMessage(".help, .say, .bind, .t(oggle) .save .load .friend");
	}
	

}
