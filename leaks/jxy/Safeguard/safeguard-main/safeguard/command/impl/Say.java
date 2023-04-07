package intentions.command.impl;

import intentions.command.Command;
import intentions.command.CommandManager;
import net.minecraft.client.Minecraft;

public class Say extends Command {

	public Say() {
		super("Say", "Say a message", "say", new String[] {"s"});
	}

	@Override
	public void onCommand(String[] args, String command) {
		boolean ddc = CommandManager.dontDoCommands;
		CommandManager.dontDoCommands = true;
		String message = "";
		for(int i=0;i<args.length;i++) {
			message += " " + args[i];
		}
		Minecraft.getMinecraft().thePlayer.sendChatMessage(args.length == 0 ? message : message.substring(1));
		CommandManager.dontDoCommands = ddc;
	}

}
