package de.verschwiegener.atero.command.commands;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;
import de.verschwiegener.atero.util.chat.ChatUtil;

public class BindCommand extends Command {

	public BindCommand() {
		super("Bind", "Binds a module to a key", new String[] {"<module>", "<key>"});
	}
	
	@Override
	public void onCommand(String[] args) {
	    switch (args[1]) {
	    case "del":
		break;
	    case "edit":
		break;
	    default:
		try {
		    Management.instance.modulemgr.getModuleByName(args[1])
			    .setKey(Keyboard.getKeyIndex(args[2].toUpperCase()));
		    ChatUtil.sendMessageWithPrefix( "Module \"" + args[1] + "\" was binded to \""+ args[2] + "\"");
		} catch (NullPointerException ex) {
		    ex.printStackTrace();
		    ChatUtil.sendMessageWithPrefix(
			    "Module \"" + args[1] + "\" could not be bindet to Key \"" + args[2] + "\"");
		}
		break;
	    }
	}

}
