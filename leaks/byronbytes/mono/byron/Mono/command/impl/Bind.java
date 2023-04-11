package byron.Mono.command.impl;

import byron.Mono.Mono;
import byron.Mono.command.Command;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

public class Bind extends Command {


    public Bind() {
        super("bind", "b", ".bind [module] [key]");
    }

    @Override
    public void onCommand(String[] args) {
        if (args.length != 2) {
            Mono.INSTANCE.sendMessage(getHelp());

        }else
        {
        try
        {
            Mono.INSTANCE.getModuleManager().getModule(args[0]).setKey(Keyboard.getKeyIndex(args[1].toUpperCase()));
            Mono.INSTANCE.sendMessage("Successfully bound " + Mono.INSTANCE.getModuleManager().getModule(args[0]).getName() + " to: " + Keyboard.getKeyName(Mono.INSTANCE.getModuleManager().getModule(args[0]).getKey()) );
        }
        catch(Exception e)
            {
                Mono.INSTANCE.sendMessage("There was an unexpected error.\nError:" + e.getMessage());
            }
        }
}

}
