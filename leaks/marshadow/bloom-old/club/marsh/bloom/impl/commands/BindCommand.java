package club.marsh.bloom.impl.commands;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.module.Module;

public class BindCommand extends Command {

	public BindCommand() {
		super("bind");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
		if(args.length > 1) {
            addMessage("Bound " + args[0] + " to " + args[1]);
            args[1] = args[1].toUpperCase();
            for (Module mod : Bloom.INSTANCE.moduleManager.getModules()) {
                if (mod.getName().replace(" ","").equalsIgnoreCase(args[0])) {
                    mod.setKeybind(Keyboard.getKeyIndex(args[1]));
                }
            }
        } else {
            addMessage(".bind [mod] [key]");
        }
	}
	

}
