package club.marsh.bloom.impl.commands;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.module.Module;

public class BindsCommand extends Command {

	public BindsCommand() {
		super("binds");
	}

	@Override
	public void onCommand(String arg, String[] args) throws Exception {
        for (Module mod : Bloom.INSTANCE.moduleManager.getModules()) {
            if (mod.keybind != Keyboard.KEY_NONE) {
                addMessage(mod.getName() + " is bound to: " + Keyboard.getKeyName(mod.keybind));
            }
        }
	}
	

}
