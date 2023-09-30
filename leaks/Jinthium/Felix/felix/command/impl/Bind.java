package felix.command.impl;

import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.command.Command;
import felix.module.Module;
import felix.util.other.Logger;

public class Bind extends Command {

    public Bind() {
        super("bind");
    }

    @Override
    public String usage() {
        return "bind <module> <bind>";
    }

    @Override
    public void executeCommand(String[] commandArguments) {
        if(commandArguments.length > 2 || commandArguments.length < 2) {
            printUsage();
        }
        if(commandArguments.length == 2) {
            String arg1 = commandArguments[0];
            String arg2 = commandArguments[1];
                if(Client.INSTANCE.getModuleManager().getModule(arg1.replaceAll("_", " ")) != null) {
                    Module module = Client.INSTANCE.getModuleManager().getModule(arg1.replaceAll("_", " "));
                    int keyIndex = Keyboard.getKeyIndex(arg2.toUpperCase());
                    module.setKeyBind(keyIndex);
                    Logger.print("Bound " + module.getName() + " to " + arg2.toUpperCase());
                } else {
                    System.out.println("invalid");
                    Logger.print("Could not find a module with that name!");
            }
        }
    }
}
