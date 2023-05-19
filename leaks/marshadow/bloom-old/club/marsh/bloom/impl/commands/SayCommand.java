package club.marsh.bloom.impl.commands;

import club.marsh.bloom.api.command.Command;

public class SayCommand extends Command {

	public SayCommand() {
		super("say");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		if(!arg.isEmpty() && !arg.startsWith(" ")) {
			mc.thePlayer.sendChatMessage(arg);
        } else {
            addMessage(".say [message]");
        }
	}
	

}
