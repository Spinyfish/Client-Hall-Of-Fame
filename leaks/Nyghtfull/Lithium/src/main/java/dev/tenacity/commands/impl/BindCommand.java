package dev.tenacity.commands.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;
import dev.tenacity.module.Module;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Binds a module to a certain key", ".bind [module] [key]");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            usage();
        } else {
            String stringModule = args[0];
            try {
                Module module = Tenacity.INSTANCE.getModuleCollection().getModuleByName(stringModule);
                module.getKeybind().setCode(Keyboard.getKeyIndex(args[1].toUpperCase()));
                sendChatWithPrefix("Set keybind for " + module.getName() + " to " + args[1].toUpperCase());
            } catch (Exception e) {
                usage();
            }
        }
    }

}
